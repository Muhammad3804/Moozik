package com.example.moozik.data

import com.example.moozik.models.Product

object ProductRepository {
    @Volatile
    private var apiGuitarProducts: List<Product> = emptyList()

    fun setProducts(products: List<Product>) {
        apiGuitarProducts = products
            .asSequence()
            .filter { isGuitar(it) }
            .toList()
    }

    fun allProducts(): List<Product> = apiGuitarProducts + hardcodedProducts()

    fun productsForCategory(category: String): List<Product> {
        return when (category) {
            "Guitar" -> apiGuitarProducts
            else -> hardcodedProducts().filter { normalizeCategory(it) == category }
        }
    }

    fun relatedProducts(current: Product, limit: Int = 8): List<Product> {
        val source = if (isApiGuitar(current)) {
            apiGuitarProducts
        } else {
            hardcodedProducts().filter { normalizeCategory(it) == normalizeCategory(current) }
        }

        return source
            .asSequence()
            .filter { it.id != current.id }
            .take(limit)
            .toList()
    }

    private fun hardcodedProducts(): List<Product> = listOf(
        Product("d1", "Yamaha Stage Custom", "PKR 120,000", "Drums", "Responsive kit for rehearsals and small gigs.", "Yamaha Stage Custom.jpg", "4.6"),
        Product("d2", "Pearl Export", "PKR 180,000", "Drums", "Durable pro-level kit with rich tone.", "Pearl Export.jpg", "4.7"),
        Product("d3", "Tama Imperialstar", "PKR 135,000", "Drums", "Excellent value beginner-to-intermediate kit.", "Tama Imperialstar.jpg", "4.5"),
        Product("d4", "Ludwig Breakbeats", "PKR 95,000", "Drums", "Compact kit perfect for small spaces.", "Ludwig Breakbeats.jpg", "4.4"),
        Product("d5", "Roland TD-1DMK", "PKR 210,000", "Drums", "Entry-level electronic kit with great features.", "Roland TD-1DMK.jpg", "4.6"),
        Product("d6", "Gretsch Catalina", "PKR 150,000", "Drums", "Classic jazz-leaning kit with warm tones.", "Gretsch Catalina.jpg", "4.6"),
        Product("d7", "Mapex Mars", "PKR 130,000", "Drums", "Versatile kit with quality hardware.", "Mapex Mars.jpg", "4.5"),
        Product("d8", "Sonor AQ2", "PKR 220,000", "Drums", "Professional-grade construction and tone.", "Sonor AQ2.jpg", "4.8"),

        Product("p1", "Casio Privia PX-160", "PKR 95,000", "Piano", "Compact digital piano with realistic key feel.", "Casio Privia PX-160.jpg", "4.5"),
        Product("p2", "Yamaha P-125", "PKR 140,000", "Piano", "Portable digital piano with clear, rich sound.", "Yamaha P-125.jpg", "4.7"),
        Product("p3", "Kawai ES110", "PKR 155,000", "Piano", "Excellent action for home practice and performance.", "Kawai ES110.jpg", "4.8"),
        Product("p4", "Roland FP-30", "PKR 160,000", "Piano", "Great piano sounds and robust build.", "Roland FP-30.jpg", "4.7"),
        Product("p5", "Nord Piano 4", "PKR 720,000", "Piano", "Stage piano used by professionals.", "Nord Piano 4.jpg", "4.9"),
        Product("p6", "Yamaha DGX-660", "PKR 185,000", "Piano", "Portable arranger piano with many voices.", "Yamaha DGX-660.jpg", "4.6"),

        Product("b1", "Fender Precision Bass", "PKR 110,000", "Bass", "Classic P-bass tone with punchy low end.", "Fender Precision Bass.jpg", "4.7"),
        Product("b2", "Ibanez SR Series", "PKR 85,000", "Bass", "Slim neck comfortable for fast playing.", "Ibanez SR Series.jpg", "4.5"),
        Product("b3", "Yamaha TRBX", "PKR 95,000", "Bass", "Balanced tone and dependable hardware.", "Yamaha TRBX.jpg", "4.6"),
        Product("b4", "Warwick RockBass", "PKR 120,000", "Bass", "Warm midrange and clear lows.", "Warwick RockBass.jpg", "4.6"),
        Product("b5", "Squier Affinity Bass", "PKR 34,000", "Bass", "Budget-friendly starter bass.", "Squier Affinity Bass.jpg", "4.2"),
        Product("b6", "ESP LTD B-204SM", "PKR 98,000", "Bass", "Active electronics for varied tones.", "ESP LTD B-204SM.jpg", "4.5"),

        Product("v1", "Stentor Student II", "PKR 18,000", "Violin", "Good beginner violin with warm tone.", "Stentor Student II.jpg", "4.1"),
        Product("v2", "Yamaha V5", "PKR 95,000", "Violin", "Professional-quality setup and sound.", "Yamaha V5.jpg", "4.6"),
        Product("v3", "Cremona SV-175", "PKR 55,000", "Violin", "Great value intermediate instrument.", "Cremona SV-175.jpg", "4.4"),
        Product("v4", "Fiddlerman Artist", "PKR 220,000", "Violin", "Rich projection and superb craftsmanship.", "Fiddlerman Artist.jpg", "4.8"),
        Product("v5", "Eastman Model", "PKR 75,000", "Violin", "Balanced tone for chamber music.", "Eastman Model.jpg", "4.5"),
        Product("v6", "Gewa Pure Violin", "PKR 130,000", "Violin", "Modern build, reliable tuning stability.", "Gewa Pure Violin.jpg", "4.7"),

        Product("o1", "Hohner Harmonica", "PKR 2,200", "Other", "Classic blues harmonica in C.", "Hohner Harmonica.jpg", "4.3"),
        Product("o2", "Kala Ukulele", "PKR 6,500", "Other", "Concert uke with warm tone.", "Kala Ukulele.jpg", "4.4"),
        Product("o3", "Meinl Cajon", "PKR 18,000", "Other", "Portable cajon with adjustable snare.", "Meinl Cajon.jpg", "4.6"),
        Product("o4", "Shure SM58", "PKR 28,000", "Other", "Industry standard vocal microphone.", "Shure SM58.jpg", "4.8"),
        Product("o5", "Fender Mustang GT-40", "PKR 82,000", "Other", "Small practice amp with great tone.", "Fender Mustang GT-40.jpg", "4.5"),
        Product("o6", "AKG K240", "PKR 150,500", "Other", "Studio headphones with balanced response.", "AKG K240.jpg", "4.6")
    )

    private fun normalizeCategory(product: Product): String {
        val haystack = "${product.category} ${product.name}".lowercase()
        return when {
            haystack.contains("guitar") -> "Guitar"
            haystack.contains("drum") -> "Drums"
            haystack.contains("piano") || haystack.contains("keyboard") -> "Piano"
            haystack.contains("bass") -> "Bass"
            haystack.contains("violin") -> "Violin"
            else -> "Other"
        }
    }

    private fun isGuitar(product: Product): Boolean = normalizeCategory(product) == "Guitar"

    private fun isApiGuitar(product: Product): Boolean = apiGuitarProducts.any { it.id == product.id }
}

