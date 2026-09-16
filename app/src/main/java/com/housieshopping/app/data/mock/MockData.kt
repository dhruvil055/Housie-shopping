package com.housieshopping.app.data.mock

import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.AddressType
import com.housieshopping.app.domain.model.Banner
import com.housieshopping.app.domain.model.Brand
import com.housieshopping.app.domain.model.Category
import com.housieshopping.app.domain.model.Coupon
import com.housieshopping.app.domain.model.CouponType
import com.housieshopping.app.domain.model.FAQItem
import com.housieshopping.app.domain.model.NotificationItem
import com.housieshopping.app.domain.model.Order
import com.housieshopping.app.domain.model.OrderItem
import com.housieshopping.app.domain.model.OrderStatus
import com.housieshopping.app.domain.model.OrderTimelineStep
import com.housieshopping.app.domain.model.Product
import com.housieshopping.app.domain.model.ProductVariant
import com.housieshopping.app.domain.model.Review
import com.housieshopping.app.domain.model.Seller
import com.housieshopping.app.domain.model.Specification
import com.housieshopping.app.domain.model.SupportTicket
import com.housieshopping.app.domain.model.TicketStatus
import com.housieshopping.app.domain.model.User

object MockData {

    val mockUser = User(
        id = "usr_101",
        name = "Rahul Sharma",
        email = "rahul.sharma@example.com",
        phone = "+91 98765 43210",
        profilePictureUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500",
        isEmailVerified = true,
        isPhoneVerified = true,
        token = "mock_jwt_token_12345"
    )

    val banners = listOf(
        Banner(
            id = "b1",
            title = "Mega Cement & TMT Sale",
            subtitle = "Up to 25% Off on Bulk Orders + Free Delivery",
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800",
            targetType = "CATEGORY",
            targetId = "cat_1"
        ),
        Banner(
            id = "b2",
            title = "Premium Bathroom Fittings",
            subtitle = "Jaquar & Hindware Luxury Collection",
            imageUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800",
            targetType = "CATEGORY",
            targetId = "cat_8"
        ),
        Banner(
            id = "b3",
            title = "Bosch Professional Power Tools",
            subtitle = "Flat 15% Cashback with Coupon BUILD2026",
            imageUrl = "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=800",
            targetType = "CATEGORY",
            targetId = "cat_6"
        )
    )

    val categories = listOf(
        Category(
            id = "cat_1",
            name = "Construction",
            imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3?w=500",
            subcategories = listOf(
                Category("cat_1_1", "Cement & Aggregate", ""),
                Category("cat_1_2", "TMT Steel Bars", ""),
                Category("cat_1_3", "Bricks & Blocks", ""),
                Category("cat_1_4", "Sand & Gravel", "")
            )
        ),
        Category(
            id = "cat_2",
            name = "Hardware",
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
            subcategories = listOf(
                Category("cat_2_1", "Screws & Fasteners", ""),
                Category("cat_2_2", "Hinges & Locks", ""),
                Category("cat_2_3", "Door Handles", ""),
                Category("cat_2_4", "Brackets & Clamps", "")
            )
        ),
        Category(
            id = "cat_3",
            name = "Electrical",
            imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
            subcategories = listOf(
                Category("cat_3_1", "Copper Wires & Cables", ""),
                Category("cat_3_2", "Switches & Sockets", ""),
                Category("cat_3_3", "MCB & Distribution", ""),
                Category("cat_3_4", "LED Lighting", "")
            )
        ),
        Category(
            id = "cat_4",
            name = "Plumbing",
            imageUrl = "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=500",
            subcategories = listOf(
                Category("cat_4_1", "PVC & CPVC Pipes", ""),
                Category("cat_4_2", "Water Tanks", ""),
                Category("cat_4_3", "Valves & Fittings", ""),
                Category("cat_4_4", "Drainage Systems", "")
            )
        ),
        Category(
            id = "cat_5",
            name = "Paint & Wall",
            imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500",
            subcategories = listOf(
                Category("cat_5_1", "Interior Emulsions", ""),
                Category("cat_5_2", "Exterior Paints", ""),
                Category("cat_5_3", "Wall Putty & Primer", ""),
                Category("cat_5_4", "Waterproofing", "")
            )
        ),
        Category(
            id = "cat_6",
            name = "Tools & Safety",
            imageUrl = "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=500"
        ),
        Category(
            id = "cat_7",
            name = "Tiles & Flooring",
            imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=500"
        ),
        Category(
            id = "cat_8",
            name = "Bathroom Fittings",
            imageUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500"
        ),
        Category(
            id = "cat_9",
            name = "Adhesives & Sealants",
            imageUrl = "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500"
        )
    )

    val brands = listOf(
        Brand("b_1", "UltraTech", "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3?w=200", true),
        Brand("b_2", "Tata Tiscon", "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=200", true),
        Brand("b_3", "Havells", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=200", true),
        Brand("b_4", "Asian Paints", "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=200", true),
        Brand("b_5", "Bosch", "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=200", true),
        Brand("b_6", "Supreme Pipes", "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=200", false),
        Brand("b_7", "Jaquar", "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=200", true)
    )

    val defaultSeller = Seller("s_1", "Housie Building Direct", 4.8, "Housie Central Warehouse #4")

    val products = listOf(
        Product(
            id = "p_1",
            title = "UltraTech Super OPC 53 Grade Cement",
            description = "High strength Ordinary Portland Cement engineered for heavy load-bearing concrete structures, columns, beams, and residential foundations.",
            brand = "UltraTech",
            categoryId = "cat_1",
            categoryName = "Construction",
            price = 380.0,
            mrp = 420.0,
            rating = 4.8,
            reviewCount = 342,
            stock = 500,
            images = listOf(
                "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3?w=800",
                "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800"
            ),
            deliveryEstimateDays = 1,
            isFeatured = true,
            isBestSeller = true,
            isDealOfDay = true,
            seller = defaultSeller,
            variants = listOf(
                ProductVariant("v_1_1", "UT-50KG", "50 kg Bag", 380.0, 420.0, 500, weight = "50 kg"),
                ProductVariant("v_1_2", "UT-1000KG", "1 Ton (20 Bags)", 7400.0, 8400.0, 25, weight = "1000 kg")
            ),
            specifications = listOf(
                Specification("Grade", "53 Grade OPC"),
                Specification("Packaging", "PP Bag"),
                Specification("Setting Time", "Initial 30 mins, Final 600 mins")
            ),
            tags = listOf("Cement", "Construction", "53 Grade", "UltraTech")
        ),
        Product(
            id = "p_2",
            title = "Tata Tiscon 550SD TMT Rebar (12mm)",
            description = "Super Ductile TMT steel bars made from pure virgin steel using advanced Tempcore technology. Superior earthquake resistance.",
            brand = "Tata Tiscon",
            categoryId = "cat_1",
            categoryName = "Construction",
            price = 620.0,
            mrp = 710.0,
            rating = 4.9,
            reviewCount = 210,
            stock = 300,
            images = listOf(
                "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800"
            ),
            deliveryEstimateDays = 1,
            isFeatured = true,
            isBestSeller = true,
            seller = defaultSeller,
            variants = listOf(
                ProductVariant("v_2_1", "TATA-8MM", "8mm Rod (12m)", 340.0, 400.0, 100),
                ProductVariant("v_2_2", "TATA-12MM", "12mm Rod (12m)", 620.0, 710.0, 300),
                ProductVariant("v_2_3", "TATA-16MM", "16mm Rod (12m)", 1100.0, 1250.0, 150)
            ),
            specifications = listOf(
                Specification("Grade", "Fe 550SD"),
                Specification("Standard", "IS 1786"),
                Specification("Length", "12 Meters")
            ),
            tags = listOf("Steel", "TMT", "Rebar", "Tata Tiscon")
        ),
        Product(
            id = "p_3",
            title = "Asian Paints Apex Ultima Exterior Emulsion (20L)",
            description = "Advanced dust-proof and weather-resistant exterior paint with silicone technology for long-lasting vibrant walls.",
            brand = "Asian Paints",
            categoryId = "cat_5",
            categoryName = "Paint & Wall",
            price = 4850.0,
            mrp = 5600.0,
            rating = 4.7,
            reviewCount = 180,
            stock = 80,
            images = listOf(
                "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=800"
            ),
            deliveryEstimateDays = 2,
            isFeatured = true,
            isDealOfDay = true,
            seller = defaultSeller,
            variants = listOf(
                ProductVariant("v_3_1", "AP-4L", "4 Litre Bucket", 1150.0, 1350.0, 50),
                ProductVariant("v_3_2", "AP-20L", "20 Litre Bucket", 4850.0, 5600.0, 80)
            ),
            specifications = listOf(
                Specification("Finish", "Soft Sheen"),
                Specification("Coverage", "55-65 sq.ft / litre (2 coats)"),
                Specification("Warranty", "7 Years")
            )
        ),
        Product(
            id = "p_4",
            title = "Bosch GSB 500W Professional Impact Drill Kit",
            description = "Compact 500W impact drill machine with 100-piece accessory set for drilling in masonry, wood, metal and steel.",
            brand = "Bosch",
            categoryId = "cat_6",
            categoryName = "Tools & Safety",
            price = 3299.0,
            mrp = 4490.0,
            rating = 4.6,
            reviewCount = 520,
            stock = 45,
            images = listOf(
                "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=800"
            ),
            deliveryEstimateDays = 1,
            isBestSeller = true,
            seller = defaultSeller,
            specifications = listOf(
                Specification("Power", "500 Watts"),
                Specification("Chuck Size", "10 mm"),
                Specification("Weight", "1.5 kg")
            )
        ),
        Product(
            id = "p_5",
            title = "Havells LifeLine Plus 1.5 sq mm Flame Retardant Wire (90m)",
            description = "100% pure electrolytic copper wire with advanced S3 technology for high insulation strength and fire protection.",
            brand = "Havells",
            categoryId = "cat_3",
            categoryName = "Electrical",
            price = 1890.0,
            mrp = 2250.0,
            rating = 4.9,
            reviewCount = 410,
            stock = 120,
            images = listOf(
                "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=800"
            ),
            deliveryEstimateDays = 2,
            seller = defaultSeller,
            variants = listOf(
                ProductVariant("v_5_1", "HAV-1.5", "1.5 sq mm (90m)", 1890.0, 2250.0, 120),
                ProductVariant("v_5_2", "HAV-2.5", "2.5 sq mm (90m)", 2850.0, 3400.0, 90),
                ProductVariant("v_5_3", "HAV-4.0", "4.0 sq mm (90m)", 4300.0, 5100.0, 60)
            )
        ),
        Product(
            id = "p_6",
            title = "Jaquar Alive Single Lever Basin Mixer Tap",
            description = "Premium chrome finished brass basin mixer tap with smooth foam flow technology and eco-water saving aerator.",
            brand = "Jaquar",
            categoryId = "cat_8",
            categoryName = "Bathroom Fittings",
            price = 2750.0,
            mrp = 3400.0,
            rating = 4.8,
            reviewCount = 145,
            stock = 35,
            images = listOf(
                "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800"
            ),
            deliveryEstimateDays = 2,
            seller = defaultSeller
        ),
        Product(
            id = "p_7",
            title = "Supreme CPVC 1 Inch SDR 11 Pressure Pipe (3m)",
            description = "Heavy duty hot and cold water plumbing pipe engineered for corrosion resistance and leak-proof jointing.",
            brand = "Supreme Pipes",
            categoryId = "cat_4",
            categoryName = "Plumbing",
            price = 320.0,
            mrp = 390.0,
            rating = 4.7,
            reviewCount = 98,
            stock = 250,
            images = listOf(
                "https://images.unsplash.com/photo-1607472586893-edb57bdc0e39?w=800"
            ),
            deliveryEstimateDays = 1,
            seller = defaultSeller
        )
    )

    val coupons = listOf(
        Coupon(
            id = "c_1",
            code = "WELCOME100",
            description = "Flat ₹100 Off on your first order above ₹999",
            type = CouponType.FIXED_AMOUNT,
            discountValue = 100.0,
            minOrderAmount = 999.0,
            expiryDate = "2026-12-31",
            isFirstOrderOnly = true
        ),
        Coupon(
            id = "c_2",
            code = "BUILD2026",
            description = "10% Off on all building materials up to ₹1000",
            type = CouponType.PERCENTAGE,
            discountValue = 10.0,
            minOrderAmount = 2500.0,
            maxDiscountAmount = 1000.0,
            expiryDate = "2026-10-15"
        ),
        Coupon(
            id = "c_3",
            code = "HOUSIE500",
            description = "Flat ₹500 Off on bulk construction orders above ₹10,000",
            type = CouponType.FIXED_AMOUNT,
            discountValue = 500.0,
            minOrderAmount = 10000.0,
            expiryDate = "2026-11-30"
        )
    )

    val savedAddresses = listOf(
        Address(
            id = "addr_1",
            fullName = "Rahul Sharma",
            phone = "+91 98765 43210",
            houseFlat = "Plot No. 42, Green Avenue",
            street = "Main MG Road, Sector 14",
            area = "DLF Phase 2",
            city = "Gurugram",
            state = "Haryana",
            pinCode = "122002",
            landmark = "Opposite Metro Station Gate 3",
            type = AddressType.HOME,
            isDefault = true
        ),
        Address(
            id = "addr_2",
            fullName = "Rahul Sharma (Site Manager)",
            phone = "+91 98765 43210",
            houseFlat = "Construction Site #8",
            street = "Outer Ring Road, Phase 3",
            area = "Electronic City",
            city = "Bengaluru",
            state = "Karnataka",
            pinCode = "560100",
            type = AddressType.SITE_LOCATION,
            isDefault = false
        )
    )

    val mockOrders = listOf(
        Order(
            id = "ord_901",
            orderNumber = "HS-2026-8841",
            createdAt = "01 Sep 2026, 10:30 AM",
            items = listOf(
                OrderItem("p_1", "UltraTech Super OPC 53 Grade Cement", "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b3?w=500", "50 kg Bag", 380.0, 10, 3800.0),
                OrderItem("p_2", "Tata Tiscon 550SD TMT Rebar (12mm)", "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=500", "12mm Rod (12m)", 620.0, 5, 3100.0)
            ),
            deliveryAddress = savedAddresses[0],
            paymentMethod = "UPI (Google Pay)",
            paymentId = "pay_982341234",
            isPaid = true,
            subtotal = 6900.0,
            discount = 500.0,
            tax = 1152.0,
            deliveryFee = 0.0,
            totalAmount = 7552.0,
            status = OrderStatus.OUT_FOR_DELIVERY,
            estimatedDeliveryDate = "Today by 4:00 PM",
            timeline = listOf(
                OrderTimelineStep(OrderStatus.PLACED, "Order Placed", "Your order has been received", "10:30 AM", true, false),
                OrderTimelineStep(OrderStatus.CONFIRMED, "Order Confirmed", "Seller verified inventory", "10:35 AM", true, false),
                OrderTimelineStep(OrderStatus.PACKING, "Packed & Loaded", "Material loaded into delivery vehicle", "11:15 AM", true, false),
                OrderTimelineStep(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", "Driver Suresh is on the way", "12:00 PM", true, true),
                OrderTimelineStep(OrderStatus.DELIVERED, "Delivered", "Delivered at site location", "Pending", false, false)
            )
        ),
        Order(
            id = "ord_902",
            orderNumber = "HS-2026-7721",
            createdAt = "28 Aug 2026, 02:15 PM",
            items = listOf(
                OrderItem("p_4", "Bosch GSB 500W Professional Impact Drill Kit", "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=500", null, 3299.0, 1, 3299.0)
            ),
            deliveryAddress = savedAddresses[0],
            paymentMethod = "Razorpay Card",
            paymentId = "pay_8812312",
            isPaid = true,
            subtotal = 3299.0,
            discount = 200.0,
            tax = 557.0,
            deliveryFee = 0.0,
            totalAmount = 3656.0,
            status = OrderStatus.DELIVERED,
            estimatedDeliveryDate = "30 Aug 2026",
            timeline = listOf(
                OrderTimelineStep(OrderStatus.DELIVERED, "Delivered", "Delivered on 30 Aug 2026", "03:45 PM", true, true)
            )
        )
    )

    val mockReviews = listOf(
        Review("r_1", "p_1", "Amit Verma", null, 5.0f, "Original Quality UltraTech Cement", "Delivered fresh stock cement bags within 24 hours. Very satisfied with Housie service!", "2 days ago"),
        Review("r_2", "p_1", "Suresh Building Constr.", null, 5.0f, "Best price for bulk order", "Saved ₹40 per bag compared to local hardware market. Seamless site delivery.", "1 week ago")
    )

    val mockFaqs = listOf(
        FAQItem("faq_1", "How do I place a bulk order for construction material?", "You can directly adjust product quantities in your cart or contact our dedicated construction manager support at 1800-HOUSIE.", "Ordering"),
        FAQItem("faq_2", "What is the return policy for defective materials?", "Damaged or wrong goods reported within 48 hours of delivery are eligible for 100% replacement or full refund.", "Returns & Refunds"),
        FAQItem("faq_3", "How long does site delivery take?", "Standard delivery takes 24-48 hours. Express same-day delivery is available for selected pin codes.", "Delivery")
    )

    val mockSupportTickets = listOf(
        SupportTicket("t_1", "TK-8831", "Delivery address change request", "Delivery", "Need to update delivery address for Order HS-2026-8841", TicketStatus.RESOLVED, "30 Aug 2026", "30 Aug 2026"),
        SupportTicket("t_2", "TK-8910", "Tax Invoice request with GSTIN", "Billing", "Please issue B2B tax invoice with GSTIN 07AAAAA0000A1Z5", TicketStatus.IN_PROGRESS, "01 Sep 2026", "01 Sep 2026")
    )

    val mockNotifications = listOf(
        NotificationItem("n_1", "🚚 Order Out for Delivery!", "Order #HS-2026-8841 is out for delivery with driver Suresh.", "10 mins ago", false, "tracking/ord_901"),
        NotificationItem("n_2", "🎉 Mega Sale Live Now!", "Get up to 25% Off on UltraTech Cement & Tata Tiscon TMT Steel.", "2 hours ago", false, "categories"),
        NotificationItem("n_3", "Coupon Received", "Use code BUILD2026 for flat 10% discount on your next order.", "1 day ago", true, "coupons")
    )
}
