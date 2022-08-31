package com.example.liointegration.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.liointegration.BuildConfig;
import com.example.liointegration.R;
import com.example.liointegration.databinding.ActivityCreateOrderBinding;

import java.util.Objects;
import java.util.UUID;

import cielo.orders.domain.CancellationRequest;
import cielo.orders.domain.CheckoutRequest;
import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.orders.domain.ResultOrders;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.cancellation.CancellationListener;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class CreateOrderActivity extends AppCompatActivity {
    private ActivityCreateOrderBinding binding;
    private Credentials credentials;
    private OrderManager orderManager;
    private Order order;
    private PaymentListener paymentListener;
    private CancellationListener cancellationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateOrderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        initSdk(getApplicationContext());

        paymentListener = new PaymentListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onPayment(@NonNull Order order) {
                order.markAsPaid();
                updateOrder(order);

                ResultOrders resultOrders = orderManager.retrieveOrders(10, 0);

                String result = "Produto: " + resultOrders.getResults().get(0).getItems().get(0).getName() + "\n Preço: R$ " + resultOrders.getResults().get(0).getItems().get(0).getUnitPrice() + "\n Quantidade: " + resultOrders.getResults().get(0).getItems().get(0).getQuantity();
                binding.result.setText(result);


                binding.callbackMessage.setText("Venda realizada com sucesso");
                binding.cancelOrderButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                binding.callbackMessage.setText("Venda cancelada");
                binding.cancelOrderButton.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
                binding.callbackMessage.setText("Erro ao finalizar venda");
                binding.cancelOrderButton.setVisibility(View.GONE);
            }
        };
        cancellationListener = new CancellationListener() {
            @Override
            public void onSuccess(@NonNull Order order) {
                binding.cancelOrderButton.setVisibility(View.GONE);
                binding.callbackMessage.setText("Pedido cancelado com sucesso");
                binding.result.setText("");
            }

            @Override
            public void onCancel() {
                binding.callbackMessage.setText("Operação de cancelamento de pedido, cancelada");
            }

            @Override
            public void onError(@NonNull PaymentError paymentError) {
                binding.callbackMessage.setText("Erro ao cancelar pedido");
            }
        };

        // Submit Payment Button
        binding.submitOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initOrder();
                    addItemToPay();
                    placeOrder();
                    payment(paymentListener, 0 );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Cancel Order Button
        binding.cancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder(cancellationListener);
            }
        });
    }

    private void initSdk(Context context) {
        credentials = new Credentials(BuildConfig.CLIENT_ID, BuildConfig.TOKEN);
        orderManager = new OrderManager(credentials, context);

        ServiceBindListener serviceBindListener = new ServiceBindListener() {
            @Override
            public void onServiceBound() {
                Log.d("onServiceBound", "Conectado");
            }

            @Override
            public void onServiceBoundError(@NonNull Throwable throwable) {
                Log.d("onServiceBoundError", throwable.getMessage());
            }

            @Override
            public void onServiceUnbound() {
                Log.d("onServiceUnbound", "Desconectado");
            }
        };

        orderManager.bind(context, serviceBindListener);
    }

    private void initOrder() throws Exception {
        order = orderManager.createDraftOrder(String.valueOf(UUID.randomUUID()));
    }

    private void addItemToPay() {
        String name = binding.productNameInput.getText().toString();

        double price = Double.parseDouble(binding.priceInput.getText().toString());
        long formattedPrice = Double.valueOf(price * 100).longValue();

        int quantity = Integer.parseInt(binding.quantityInput.getText().toString());

        order.addItem("1", name, formattedPrice, quantity, "UNIDADE");
    }

    private void placeOrder() {
        orderManager.placeOrder(order);
    }

    private void payment(PaymentListener paymentListener, int installments) {
        CheckoutRequest request = new CheckoutRequest.Builder()
                .orderId(order.getId())
                .amount(order.getPrice())
                .installments(installments)
                .paymentCode(PaymentCode.CREDITO_AVISTA)
                .build();

        orderManager.checkoutOrder(request, paymentListener);
    }

    private void updateOrder(Order order) {
        this.order = order;
        orderManager.updateOrder(this.order);
    }

    private void cancelOrder(CancellationListener cancellationListener) {
        CancellationRequest request = new CancellationRequest.Builder()
                .orderId(order.getId())
                .authCode(order.getPayments().get(0).getAuthCode())
                .cieloCode(order.getPayments().get(0).getCieloCode())
                .value(order.getPayments().get(0).getAmount())
                .build();

        orderManager.cancelOrder(request, cancellationListener);
    }

    @Override
    public void onBackPressed() {
        orderManager.unbind();
        super.onBackPressed();
    }
}