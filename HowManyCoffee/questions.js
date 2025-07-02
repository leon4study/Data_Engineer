const questions = [
  {
    id: "Q1_LOCATION",
    text: "어느 지역에서 창업하시겠습니까?",
    options: [
      { label: "연남동", cost: 150000 },
      { label: "여의도", cost: 130000 },
      { label: "남양주", cost: 50000 },
      { label: "가평 리버뷰", cost: 40000 },
      { label: "강남", cost: 200000 },
    ],
  },
  {
    id: "Q2_SIZE",
    text: "매장 크기는 어떻게 하시겠습니까?",
    options: [
      { label: "소형", cost: 0 }, // staffCost 등 상황에 따라 금액 변경 가능
      { label: "중형", cost: 1200000 },
      { label: "대형", cost: 4000000 },
      { label: "테이크아웃 전문", cost: 0 },
    ],
  },
  {
    id: "Q3_COFFEE",
    text: "커피 전략을 선택해주세요.",
    options: [
      { label: "일반 머신 + 저가 원두", cost: 3300000 }, // machineCost + ingredientCost 대략 합산
      { label: "준수한 머신 + 좋은 원두", cost: 6600000 },
      { label: "직접 로스팅", cost: 17800000 },
      { label: "스페셜티 + 라떼아트", cost: 12900000 },
    ],
  },
  {
    id: "Q4_DESSERT",
    text: "디저트 전략을 선택해주세요.",
    options: [
      { label: "없음", cost: 0 },
      { label: "납품 디저트", cost: 1000000 },
      { label: "제과사 고용", cost: 7700000 },
      { label: "냉동 디저트 + 데코", cost: 1400000 },
    ],
  },
  {
    id: "Q5_INTERIOR",
    text: "인테리어 스타일을 선택해주세요.",
    options: [
      { label: "셀프 인테리어", cost: 300000 },
      { label: "일반 인테리어", cost: 1000000 },
      { label: "감성 카페", cost: 2000000 },
      { label: "SNS 맛집", cost: 3000000 },
    ],
  },
  {
    id: "Q6_PRICE",
    text: "커피 가격을 선택해주세요.",
    options: [
      { label: "4,000원", cost: 4000 },
      { label: "4,800원", cost: 4800 },
      { label: "6,000원", cost: 6000 },
      { label: "7,500원", cost: 7500 },
    ],
  },
  {
    id: "Q7_MANPOWER",
    text: "인력을 선택해주세요.",
    options: [
      { label: "혼자 운영", cost: 0 },
      { label: "알바 1명", cost: 1200000 },
      { label: "알바 2명", cost: 2400000 },
      { label: "바리스타 + 알바", cost: 4000000 },
    ],
  },
  {
    id: "Q8_MARKETING",
    text: "마케팅 수준을 선택해주세요.",
    options: [
      { label: "마케팅 안 함", cost: 0 },
      { label: "인스타/블로그", cost: 200000 },
      { label: "지역 광고 포함", cost: 600000 },
      { label: "적극 광고", cost: 1200000 },
    ],
  },
];
