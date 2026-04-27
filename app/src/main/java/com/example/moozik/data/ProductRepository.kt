package com.example.moozik.data

import com.example.moozik.R
import com.example.moozik.models.Product

object ProductRepository {
    fun allProducts(): List<Product> = listOf(
        Product("g1", "Fender Stratocaster", "Guitar", "PKR 145,000", 4.8, "Classic Fender Strat. Alder body, maple neck. Great for rock and blues.", R.drawable.logo),
        Product("g2", "Gibson Les Paul", "Guitar", "PKR 220,000", 4.9, "Mahogany body with carved maple top, warm sustain for leads and rhythm.", R.drawable.logo),
        Product("g3", "Yamaha FG800", "Guitar", "PKR 48,500", 4.6, "Solid top acoustic guitar, easy playability for beginners.", R.drawable.logo),
        Product("g4", "Ibanez RG Series", "Guitar", "PKR 98,000", 4.7, "High-performance neck, double-lock tremolo, great for shredding.", R.drawable.logo),
        Product("g5", "Taylor 214ce", "Guitar", "PKR 210,000", 4.9, "Grand Auditorium acoustic-electric with rich tone and onboard electronics.", R.drawable.logo),
        Product("g6", "PRS SE Custom 24", "Guitar", "PKR 160,000", 4.8, "Versatile player's guitar with great contour and balanced pickups.", R.drawable.logo),
        Product("g7", "Epiphone Casino", "Guitar", "PKR 120,000", 4.5, "Hollow body with warm jazzy tones and vintage vibe.", R.drawable.logo),
        Product("g8", "Fender Telecaster", "Guitar", "PKR 140,000", 4.7, "Bright twangy tones, ideal for country and pop.", R.drawable.logo),
        Product("g9", "Martin D-15M", "Guitar", "PKR 260,000", 4.9, "Dreadnought acoustic with amazing projection and tone.", R.drawable.logo),
        Product("g10", "Squier Classic Vibe", "Guitar", "PKR 34,000", 4.4, "Affordable classic-sounding guitar for students.", R.drawable.logo),

        Product("d1", "Yamaha Stage Custom", "Drums", "PKR 120,000", 4.6, "Responsive kit for rehearsals and small gigs.", R.drawable.logo),
        Product("d2", "Pearl Export", "Drums", "PKR 180,000", 4.7, "Durable pro-level kit with rich tone.", R.drawable.logo),
        Product("d3", "Tama Imperialstar", "Drums", "PKR 135,000", 4.5, "Excellent value beginner-to-intermediate kit.", R.drawable.logo),
        Product("d4", "Ludwig Breakbeats", "Drums", "PKR 95,000", 4.4, "Compact kit perfect for small spaces.", R.drawable.logo),
        Product("d5", "Roland TD-1DMK", "Drums", "PKR 210,000", 4.6, "Entry-level electronic kit with great features.", R.drawable.logo),
        Product("d6", "Gretsch Catalina", "Drums", "PKR 150,000", 4.6, "Classic jazz-leaning kit with warm tones.", R.drawable.logo),
        Product("d7", "Mapex Mars", "Drums", "PKR 130,000", 4.5, "Versatile kit with quality hardware.", R.drawable.logo),
        Product("d8", "Sonor AQ2", "Drums", "PKR 220,000", 4.8, "Professional-grade construction and tone.", R.drawable.logo),

        Product("p1", "Casio Privia PX-160", "Piano", "PKR 95,000", 4.5, "Compact digital piano with realistic key feel.", R.drawable.logo),
        Product("p2", "Yamaha P-125", "Piano", "PKR 140,000", 4.7, "Portable digital piano with clear, rich sound.", R.drawable.logo),
        Product("p3", "Kawai ES110", "Piano", "PKR 155,000", 4.8, "Excellent action for home practice and performance.", R.drawable.logo),
        Product("p4", "Roland FP-30", "Piano", "PKR 160,000", 4.7, "Great piano sounds and robust build.", R.drawable.logo),
        Product("p5", "Nord Piano 4", "Piano", "PKR 720,000", 4.9, "Stage piano used by professionals.", R.drawable.logo),
        Product("p6", "Yamaha DGX-660", "Piano", "PKR 185,000", 4.6, "Portable arranger piano with many voices.", R.drawable.logo),

        Product("b1", "Fender Precision Bass", "Bass", "PKR 110,000", 4.7, "Classic P-bass tone with punchy low end.", R.drawable.logo),
        Product("b2", "Ibanez SR Series", "Bass", "PKR 85,000", 4.5, "Slim neck comfortable for fast playing.", R.drawable.logo),
        Product("b3", "Yamaha TRBX", "Bass", "PKR 95,000", 4.6, "Balanced tone and dependable hardware.", R.drawable.logo),
        Product("b4", "Warwick RockBass", "Bass", "PKR 120,000", 4.6, "Warm midrange and clear lows.", R.drawable.logo),
        Product("b5", "Squier Affinity Bass", "Bass", "PKR 34,000", 4.2, "Budget-friendly starter bass.", R.drawable.logo),
        Product("b6", "ESP LTD B-204SM", "Bass", "PKR 98,000", 4.5, "Active electronics for varied tones.", R.drawable.logo),

        Product("v1", "Stentor Student II", "Violin", "PKR 18,000", 4.1, "Good beginner violin with warm tone.", R.drawable.logo),
        Product("v2", "Yamaha V5", "Violin", "PKR 95,000", 4.6, "Professional-quality setup and sound.", R.drawable.logo),
        Product("v3", "Cremona SV-175", "Violin", "PKR 55,000", 4.4, "Great value intermediate instrument.", R.drawable.logo),
        Product("v4", "Fiddlerman Artist", "Violin", "PKR 220,000", 4.8, "Rich projection and superb craftsmanship.", R.drawable.logo),
        Product("v5", "Eastman Model", "Violin", "PKR 75,000", 4.5, "Balanced tone for chamber music.", R.drawable.logo),
        Product("v6", "Gewa Pure Violin", "Violin", "PKR 130,000", 4.7, "Modern build, reliable tuning stability.", R.drawable.logo),

        Product("o1", "Hohner Harmonica", "Other", "PKR 2,200", 4.3, "Classic blues harmonica in C.", R.drawable.logo),
        Product("o2", "Kala Ukulele", "Other", "PKR 6,500", 4.4, "Concert uke with warm tone.", R.drawable.logo),
        Product("o3", "Meinl Cajon", "Other", "PKR 18,000", 4.6, "Portable cajon with adjustable snare.", R.drawable.logo),
        Product("o4", "Shure SM58", "Other", "PKR 28,000", 4.8, "Industry standard vocal microphone.", R.drawable.logo),
        Product("o5", "Boss Katana Mini", "Other", "PKR 22,000", 4.5, "Small practice amp with great tone.", R.drawable.logo),
        Product("o6", "AKG K240", "Other", "PKR 18,500", 4.6, "Studio headphones with balanced response.", R.drawable.logo)
    )
}

