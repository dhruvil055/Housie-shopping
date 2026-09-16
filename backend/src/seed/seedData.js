import { ROLES } from '../config/constants.js';

export const seedUsers = [
  {
    name: 'Main Store Administrator',
    email: 'admin@housieshopping.com',
    phone: '+91 99999 88888',
    password: 'password123',
    role: ROLES.SUPER_ADMIN,
    isEmailVerified: true,
    isPhoneVerified: true
  },
  {
    name: 'Operations Manager',
    email: 'manager@housieshopping.com',
    phone: '+91 99999 77777',
    password: 'password123',
    role: ROLES.MANAGER,
    isEmailVerified: true,
    isPhoneVerified: true
  },
  {
    name: 'Rahul Sharma',
    email: 'rahul.sharma@example.com',
    phone: '+91 98765 43210',
    password: 'password123',
    role: ROLES.CUSTOMER,
    isEmailVerified: true,
    isPhoneVerified: true,
    addresses: [
      {
        fullName: 'Rahul Sharma',
        phone: '+91 98765 43210',
        houseFlat: 'Tower 4, Flat 1202',
        street: 'Golf Course Extension Road',
        area: 'Sector 65',
        city: 'Gurugram',
        state: 'Haryana',
        postalCode: '122018',
        landmark: 'Near Worldmark Mall',
        lat: 28.4089,
        lng: 77.0678,
        addressType: 'Home',
        isDefault: true
      },
      {
        fullName: 'Rahul Sharma (Site Office)',
        phone: '+91 98765 43210',
        houseFlat: 'Plot 45, Site B',
        street: 'Sohna Road',
        area: 'Badshahpur',
        city: 'Gurugram',
        state: 'Haryana',
        postalCode: '122101',
        landmark: 'Near Subhash Chowk',
        lat: 28.3842,
        lng: 77.0425,
        addressType: 'Work',
        isDefault: false
      }
    ]
  }
];

export const seedCategories = [
  {
    name: 'Cement & Concrete',
    slug: 'cement',
    description: 'High strength OPC, PPC, and ready-mix concrete',
    iconUrl: 'https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500',
    productCount: 15,
    displayOrder: 1,
    subcategories: [
      { name: 'OPC 53 Grade', slug: 'opc-53', productCount: 6 },
      { name: 'PPC Cement', slug: 'ppc', productCount: 5 },
      { name: 'White Cement & Wall Putty', slug: 'white-cement', productCount: 4 }
    ]
  },
  {
    name: 'Steel & TMT Bars',
    slug: 'steel-tmt',
    description: 'Primary structural steel rebars, binding wires and mesh',
    iconUrl: 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=500',
    productCount: 22,
    displayOrder: 2,
    subcategories: [
      { name: 'TMT 550D Rebars', slug: 'tmt-550d', productCount: 10 },
      { name: 'Binding Wire', slug: 'binding-wire', productCount: 6 },
      { name: 'Structural MS Channels', slug: 'ms-channels', productCount: 6 }
    ]
  },
  {
    name: 'Paints & Wall Care',
    slug: 'paints',
    description: 'Interior, exterior emulsions, primers, and waterproof coatings',
    iconUrl: 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500',
    productCount: 30,
    displayOrder: 3,
    subcategories: [
      { name: 'Exterior Weatherproof', slug: 'exterior-paints', productCount: 12 },
      { name: 'Interior Luxury Emulsions', slug: 'interior-paints', productCount: 10 },
      { name: 'Waterproofing Chemical', slug: 'waterproofing', productCount: 8 }
    ]
  },
  {
    name: 'Bricks & Aggregates',
    slug: 'bricks-aggregates',
    description: 'First-class red clay bricks, AAC lightweight blocks, and river sand',
    iconUrl: 'https://images.unsplash.com/photo-1590069261209-f8e9b8642343?w=500',
    productCount: 18,
    displayOrder: 4,
    subcategories: [
      { name: 'Red Clay Bricks', slug: 'red-bricks', productCount: 6 },
      { name: 'AAC Blocks', slug: 'aac-blocks', productCount: 6 },
      { name: 'Coarse Sand & Gravel', slug: 'aggregates', productCount: 6 }
    ]
  },
  {
    name: 'Power Tools & Equipment',
    slug: 'power-tools',
    description: 'Heavy duty drills, angle grinders, circular saws, and tile cutters',
    iconUrl: 'https://images.unsplash.com/photo-1504148455328-c376907d081c?w=500',
    productCount: 25,
    displayOrder: 5,
    subcategories: [
      { name: 'Drilling & Hammering', slug: 'drills', productCount: 10 },
      { name: 'Cutting & Grinding', slug: 'grinders', productCount: 8 },
      { name: 'Measurement Lasers', slug: 'measurement', productCount: 7 }
    ]
  }
];

export const seedProducts = [
  {
    sku: 'CEM-ULT-53',
    title: 'UltraTech Super OPC 53 Grade Cement (50 kg)',
    description: 'UltraTech 53 Grade Ordinary Portland Cement engineered for heavy residential structural columns, beams, slabs, and precast concrete works. Delivers ultra-rapid setting and maximum compressive strength exceeding 53 MPa.',
    brand: 'UltraTech',
    categoryId: 'cement',
    categoryName: 'Cement & Concrete',
    price: 380.0,
    mrp: 420.0,
    stock: 250,
    images: [
      'https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=600',
      'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=600'
    ],
    deliveryEstimateDays: 1,
    isFeatured: true,
    isBestSeller: true,
    isDealOfDay: false,
    rating: 4.8,
    reviewCount: 48,
    variants: [
      { name: '50 kg Bag', price: 380.0, mrp: 420.0, stock: 250, unit: '50kg Bag' },
      { name: 'Pallet (40 Bags)', price: 14800.0, mrp: 16800.0, stock: 15, unit: 'Pallet' }
    ],
    specifications: [
      { key: 'Grade', value: 'OPC 53 Grade (IS 12269)' },
      { key: 'Setting Time (Initial)', value: '60 minutes minimum' },
      { key: 'Compressive Strength', value: '53 MPa @ 28 Days' },
      { key: 'Packaging', value: 'Tamper-Proof Laminated Polypropylene Bag' }
    ],
    tags: ['cement', 'ultratech', 'opc53', 'construction', 'foundation']
  },
  {
    sku: 'STL-TAT-12',
    title: 'Tata Tiscon 550SD Super Ductile TMT Rebar (12mm)',
    description: 'Tata Tiscon 550SD high ductile seismic-resistant thermo-mechanically treated steel rebar. Superior rib design ensures unmatched grip with concrete, higher elongation for earthquake resistance and lower carbon footprint.',
    brand: 'Tata Tiscon',
    categoryId: 'steel-tmt',
    categoryName: 'Steel & TMT Bars',
    price: 640.0,
    mrp: 720.0,
    stock: 400,
    images: [
      'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=600',
      'https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=600'
    ],
    deliveryEstimateDays: 1,
    isFeatured: true,
    isBestSeller: true,
    isDealOfDay: true,
    rating: 4.9,
    reviewCount: 64,
    variants: [
      { name: '12mm (12m Rod)', price: 640.0, mrp: 720.0, stock: 400, unit: '1 Rod' },
      { name: '10mm (12m Rod)', price: 460.0, mrp: 520.0, stock: 350, unit: '1 Rod' },
      { name: '16mm (12m Rod)', price: 1120.0, mrp: 1250.0, stock: 200, unit: '1 Rod' }
    ],
    specifications: [
      { key: 'Grade', value: 'Fe 550D Super Ductile (IS 1786)' },
      { key: 'Yield Strength', value: '550 N/mm² minimum' },
      { key: 'Elongation', value: '16% minimum' },
      { key: 'Standard Length', value: '12 Meters' }
    ],
    tags: ['steel', 'tmt', 'tata', 'tiscon', 'rebar', 'reinforcement']
  },
  {
    sku: 'PNT-ASN-20L',
    title: 'Asian Paints Apex Ultima Exterior Emulsion (20 Litres)',
    description: 'Apex Ultima exterior wall paint equipped with silicone additives and dirt-pick-up resistance. Protects your building facade against intense UV, heavy rains, fungal and algae growth for 7+ years guaranteed.',
    brand: 'Asian Paints',
    categoryId: 'paints',
    categoryName: 'Paints & Wall Care',
    price: 4850.0,
    mrp: 5600.0,
    stock: 65,
    images: [
      'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=600'
    ],
    deliveryEstimateDays: 2,
    isFeatured: true,
    isBestSeller: false,
    isDealOfDay: true,
    rating: 4.7,
    reviewCount: 32,
    variants: [
      { name: '20 Litre Bucket', price: 4850.0, mrp: 5600.0, stock: 65, unit: 'Bucket' },
      { name: '10 Litre Bucket', price: 2600.0, mrp: 3000.0, stock: 40, unit: 'Bucket' },
      { name: '4 Litre Can', price: 1150.0, mrp: 1350.0, stock: 80, unit: 'Can' }
    ],
    specifications: [
      { key: 'Finish', value: 'Soft Sheen' },
      { key: 'Coverage', value: '55-60 sq.ft / Litre (2 coats)' },
      { key: 'Warranty', value: '7 Years Performance Warranty' },
      { key: 'VOC Content', value: 'Low VOC (< 50 gm/lt)' }
    ],
    tags: ['paint', 'asian-paints', 'exterior', 'emulsion', 'waterproof']
  },
  {
    sku: 'BRK-RED-1K',
    title: 'First Class Red Clay Bricks (Kiln Baked - 1000 Pieces)',
    description: 'High compressive strength first-class red clay bricks machine-molded and kiln-baked for uniform copper color, sharp edges, and zero efflorescence. Ideal for masonry load-bearing walls.',
    brand: 'Housie Direct',
    categoryId: 'bricks-aggregates',
    categoryName: 'Bricks & Aggregates',
    price: 9500.0,
    mrp: 11000.0,
    stock: 50,
    images: [
      'https://images.unsplash.com/photo-1590069261209-f8e9b8642343?w=600'
    ],
    deliveryEstimateDays: 1,
    isFeatured: false,
    isBestSeller: true,
    isDealOfDay: false,
    rating: 4.6,
    reviewCount: 28,
    variants: [
      { name: '1000 Pieces Truckload', price: 9500.0, mrp: 11000.0, stock: 50, unit: '1000 Pcs' }
    ],
    specifications: [
      { key: 'Crushing Strength', value: '10.5 N/mm²' },
      { key: 'Water Absorption', value: 'Less than 15%' },
      { key: 'Dimensions', value: '190 x 90 x 90 mm' }
    ],
    tags: ['bricks', 'red-clay', 'masonry', 'walls']
  },
  {
    sku: 'TLS-BOS-GBH',
    title: 'Bosch GBH 220 Professional Rotary Hammer Drill (720W)',
    description: 'Bosch 720W heavy rotary hammer with 2.0 Joules impact energy. Three operating modes: drilling, hammer drilling, and chiseling in tough reinforced concrete and stone.',
    brand: 'Bosch',
    categoryId: 'power-tools',
    categoryName: 'Power Tools & Equipment',
    price: 5200.0,
    mrp: 6400.0,
    stock: 45,
    images: [
      'https://images.unsplash.com/photo-1504148455328-c376907d081c?w=600'
    ],
    deliveryEstimateDays: 2,
    isFeatured: true,
    isBestSeller: true,
    isDealOfDay: false,
    rating: 4.9,
    reviewCount: 52,
    variants: [
      { name: 'Drill Kit with 3 SDS Bits', price: 5200.0, mrp: 6400.0, stock: 45, unit: 'Complete Kit' }
    ],
    specifications: [
      { key: 'Rated Input Power', value: '720 Watts' },
      { key: 'Impact Energy', value: '2.0 Joules' },
      { key: 'Drilling Dia. Concrete', value: '4 - 22 mm' },
      { key: 'Weight', value: '2.3 kg' }
    ],
    tags: ['bosch', 'drill', 'rotary-hammer', 'power-tools', 'concrete']
  }
];

export const seedBanners = [
  {
    title: 'Mega Cement & TMT Sale',
    subtitle: 'Up to 25% Off on Bulk Orders + Free Direct Site Delivery',
    imageUrl: 'https://images.unsplash.com/photo-1541888946425-d0fbb186a5b2?w=800',
    targetType: 'CATEGORY',
    targetId: 'cement',
    categoryTarget: 'cement',
    displayOrder: 1,
    isActive: true
  },
  {
    title: 'Asian Paints Monsoon Protect',
    subtitle: 'Exterior Emulsion with 7-Year Waterproofing Warranty',
    imageUrl: 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=800',
    targetType: 'CATEGORY',
    targetId: 'paints',
    categoryTarget: 'paints',
    displayOrder: 2,
    isActive: true
  },
  {
    title: 'Bosch Contractor Gear',
    subtitle: 'Flat 15% Cashback with Coupon BUILD500',
    imageUrl: 'https://images.unsplash.com/photo-1504148455328-c376907d081c?w=800',
    targetType: 'CATEGORY',
    targetId: 'power-tools',
    categoryTarget: 'power-tools',
    displayOrder: 3,
    isActive: true
  }
];

export const seedCoupons = [
  {
    code: 'BUILD500',
    description: 'Flat ₹500 off on building materials above ₹3,000',
    discountType: 'FIXED_AMOUNT',
    discountValue: 500.0,
    minOrderAmount: 3000.0,
    maxDiscountAmount: 500.0,
    validUntil: new Date('2027-12-31'),
    maxUses: 5000,
    isActive: true
  },
  {
    code: 'STEELDEAL',
    description: '12% discount on bulk steel orders above ₹10,000',
    discountType: 'PERCENTAGE',
    discountValue: 12.0,
    minOrderAmount: 10000.0,
    maxDiscountAmount: 2500.0,
    validUntil: new Date('2027-12-31'),
    maxUses: 1000,
    isActive: true
  },
  {
    code: 'HOUSIE100',
    description: 'Welcome gift: ₹100 off on your first order',
    discountType: 'FIXED_AMOUNT',
    discountValue: 100.0,
    minOrderAmount: 1000.0,
    maxDiscountAmount: 100.0,
    validUntil: new Date('2027-12-31'),
    maxUses: 10000,
    isActive: true
  }
];
