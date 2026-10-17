package com.example.lumastudio.utils;

import android.graphics.Color;

import com.example.lumastudio.models.FrameModel;

import java.util.ArrayList;
import java.util.List;

public class FrameCatalogManager {

    public static List<FrameModel> getAllFrames() {
        List<FrameModel> list = new ArrayList<>();

        list.add(new FrameModel(
                "f_love_coquette", "Coquette Love Ribbon", "Aesthetic",
                4, "3:4", Color.parseColor("#E8A0BF"), Color.parseColor("#FDF2F5"),
                "Coquette Hearts", "Bingkai romantis estetik pita dan hati merah muda ala Coquette."
        ));

        list.add(new FrameModel(
                "f_romantic_blush", "Romantic Blush Love", "Couple & Friends",
                3, "Strip", Color.parseColor("#D97768"), Color.parseColor("#F5F0E6"),
                "Love Rose", "Desain bingkai strip cinta romantis bernuansa warna mawar dan cream hangat."
        ));

        list.add(new FrameModel(
                "f_y2k_purple", "Y2K Cyber Heart", "Vintage",
                2, "1:1", Color.parseColor("#D8B4F8"), Color.parseColor("#1A181B"),
                "Y2K Heart", "Bingkai 2-shot hati Y2K retro dengan paduan warna ungu cybergoth."
        ));

        list.add(new FrameModel(
                "f_classic_3", "Classic Strip 3-Shot", "Classic Photobooth",
                3, "Strip", Color.parseColor("#1D4542"), Color.parseColor("#F5F0E6"),
                "Solid", "Bingkai photobooth vertikal klasik dengan 3 slot foto dan latar cream."
        ));

        list.add(new FrameModel(
                "f_vintage_4", "90s Retro Film", "Vintage",
                4, "3:4", Color.parseColor("#343A40"), Color.parseColor("#FAF6F0"),
                "Vintage Retro", "Karakter film retro 90-an dengan border tebal dan efek grain hangat."
        ));

        list.add(new FrameModel(
                "f_minimalist_2", "Minimalist Clean", "Minimalist",
                2, "1:1", Color.parseColor("#243B31"), Color.parseColor("#FFFFFF"),
                "Solid", "Desain minimalis bersih dengan 2 slot foto persegi yang elegan."
        ));

        list.add(new FrameModel(
                "f_aesthetic_3", "Korean Pastel Dream", "Aesthetic",
                3, "Strip", Color.parseColor("#F8E8EE"), Color.parseColor("#FFFDF9"),
                "Korean Pastel", "Nuansa pastel lembut ala photobooth Korea dengan ornamen estetik."
        ));

        list.add(new FrameModel(
                "f_nature_3", "Sage Botanical", "Nature",
                3, "Strip", Color.parseColor("#6B8F71"), Color.parseColor("#F5F0E6"),
                "Sage Green", "Tema hijau sage alami yang menenangkan untuk momen santai di alam."
        ));

        list.add(new FrameModel(
                "f_celebration_4", "Party Celebration", "Celebration",
                4, "3:4", Color.parseColor("#80634D"), Color.parseColor("#FFFDF9"),
                "Vintage Mocha", "Bingkai spesial perayaan ulang tahun dan pesta meriah."
        ));

        list.add(new FrameModel(
                "f_film_6", "Disposable Film 6-Shot", "Film Camera",
                6, "Strip", Color.parseColor("#101C1C"), Color.parseColor("#E0ECEC"),
                "Solid", "Layout strip panjang dengan 6 slot foto ala kamera sekali pakai."
        ));

        return list;
    }

    public static List<String> getCategories() {
        List<String> cats = new ArrayList<>();
        cats.add("Semua");
        cats.add("Aesthetic");
        cats.add("Couple & Friends");
        cats.add("Classic Photobooth");
        cats.add("Vintage");
        cats.add("Minimalist");
        cats.add("Nature");
        cats.add("Celebration");
        cats.add("Film Camera");
        return cats;
    }
}