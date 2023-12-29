package com.example.Others;

import javafx.scene.control.Alert;
import java.util.Set;

public class PaymentManager {
    public static final int SEAT_PRICE = 25000;

    public static boolean processPayment(Set<Integer> selectedSeats, double amountPaid, double totalAmount) {
        if (amountPaid >= totalAmount) {
            double change = amountPaid - totalAmount;
            showPaymentSuccessDialog(totalAmount, change);
            return true;
        } else {
            showPaymentFailureDialog(totalAmount, amountPaid);
            return false;
        }
    }

    private static void showPaymentSuccessDialog(double totalAmount, double change) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pembayaran Berhasil");
        alert.setHeaderText(null);
        alert.setContentText("Pembayaran berhasil!\nTotal harga: " + totalAmount + "\nKembalian: " + change);
        alert.showAndWait();
    }

    private static void showPaymentFailureDialog(double totalAmount, double amountPaid) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Pembayaran Gagal");
        alert.setHeaderText(null);
        alert.setContentText("Pembayaran gagal tidak cukup dana.\nDana kurang: " + (totalAmount - amountPaid));
        alert.showAndWait();
    }
}
