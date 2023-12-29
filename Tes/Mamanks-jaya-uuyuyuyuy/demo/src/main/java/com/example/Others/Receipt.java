package com.example.Others;

import java.util.HashSet;
import java.util.Set;

public class Receipt {
    private String pembeli;
    private String jamPenayangan;
    private String film;
    private Set<Integer> kursiPilihanUser;

    public Receipt(String pembeli, String jamPenayangan, String film, Set<Integer> kursiPilihanUser) {
        this.pembeli = pembeli;
        this.jamPenayangan = jamPenayangan;
        this.film = film;
        this.kursiPilihanUser = new HashSet<>(kursiPilihanUser); // Create a new set
    }

    public String getNama() {
        return pembeli;
    }

    public String getJamPenayangan() {
        return jamPenayangan;
    }

    public String getFilm() {
        return film;
    }

    public Set<Integer> getKursiPilihanUser() {
        return kursiPilihanUser;
    }

    public String generateReceipt() {
        StringBuilder nota = new StringBuilder();
        nota.append("\n");
        nota.append("===== Nota Pemesanan =====\n");
        nota.append("Nama Pembeli: ").append(pembeli).append("\n");
        nota.append("Jam Penayangan: ").append(jamPenayangan).append("\n");
        nota.append("Nama Film: ").append(film).append("\n");
        nota.append("Kursi Terpilih: ");

        // Cetak nomor kursi yang dipilih oleh pengguna
        for (int kursi : kursiPilihanUser) {
            nota.append("Kursi ").append(kursi).append(" ");
        }

        return nota.toString();
    }

    public void setNama(String updatedName) {
        this.pembeli = updatedName;
    }

    public void setJamP(String newShowtime) {
        this.jamPenayangan = newShowtime;
    }
}