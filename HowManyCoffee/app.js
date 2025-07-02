import { createClient } from "https://cdn.jsdelivr.net/npm/@supabase/supabase-js/+esm";

const supabaseUrl = "https://igsacelpffgejmquacci.supabase.co";
const supabaseAnonKey =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlnc2FjZWxwZmZnZWptcXVhY2NpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyNjc0NDYsImV4cCI6MjA2Njg0MzQ0Nn0.WgwpS1ANNk_Nr0GyKmplzJh51DLMi-au19sAFu4cMn0";
// Supabase 연결
const supabase = createClient(supabaseUrl, supabaseAnonKey);

// 세션 ID 관리
async function createSession() {
  const userAgent = navigator.userAgent;
  const referrer = document.referrer;

  const { data, error } = await supabase
    .from("sessions")
    .insert([{ user_agent: userAgent }])
    .select()
    .single();

  if (error) {
    console.error("세션 생성 오류:", error.message);
    return null;
  }

  const sessionId = data.session_id;
  localStorage.setItem("session_id", sessionId);
  return sessionId;
}

// 이벤트 로그 저장
async function logEvent(eventType, metadata = {}) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  await supabase.from("events").insert([
    {
      session_id: sessionId,
      event_type: eventType,
      metadata,
    },
  ]);
}

// 질문 응답 저장 (응답 시간 포함)
async function saveResponse(questionCode, choiceCode, choiceIndex = null) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  await supabase.from("responses").insert([
    {
      session_id: sessionId,
      question_code: questionCode,
      choice_code: choiceCode,
      choice_index: choiceIndex,
    },
  ]);
}

// 선택지 매핑 객체 (생략, 이전과 동일)
const regionRentMap = {
  연남동: 150000,
  여의도: 130000,
  남양주: 50000,
  "가평 리버뷰": 40000,
  강남: 200000,
};

const sizeMap = {
  소형: { pyeong: 10, staffCost: 0 },
  중형: { pyeong: 30, staffCost: 1200000 },
  대형: { pyeong: 100, staffCost: 4000000 },
  "테이크아웃 전문": { pyeong: 5, staffCost: 0 },
};

const coffeeStrategyMap = {
  "일반 머신 + 저가 원두": {
    machineCost: 3000000,
    ingredientCost: 300000,
    baristaCost: 0,
  },
  "준수한 머신 + 좋은 원두": {
    machineCost: 6000000,
    ingredientCost: 600000,
    baristaCost: 0,
  },
  "직접 로스팅": {
    machineCost: 15000000,
    ingredientCost: 800000,
    baristaCost: 2000000,
  },
  "스페셜티 + 라떼아트": {
    machineCost: 10000000,
    ingredientCost: 700000,
    baristaCost: 2000000,
  },
};

const dessertStrategyMap = {
  없음: { dessertMachineCost: 0, dessertIngredientCost: 0, pastryCost: 0 },
  "납품 디저트": {
    dessertMachineCost: 500000,
    dessertIngredientCost: 500000,
    pastryCost: 0,
  },
  "제과사 고용": {
    dessertMachineCost: 5000000,
    dessertIngredientCost: 700000,
    pastryCost: 2000000,
  },
  "냉동 디저트 + 데코": {
    dessertMachineCost: 1000000,
    dessertIngredientCost: 400000,
    pastryCost: 0,
  },
};

const interiorMap = {
  "셀프 인테리어": 300000,
  "일반 인테리어": 1000000,
  "감성 카페": 2000000,
  "SNS 맛집": 3000000,
};

const coffeePriceMap = {
  "4,000원": 4000,
  "4,800원": 4800,
  "6,000원": 6000,
  "7,500원": 7500,
};

const manpowerMap = {
  "혼자 운영": 0,
  "알바 1명": 1200000,
  "알바 2명": 2400000,
  "바리스타 + 알바": 4000000,
};

const marketingMap = {
  "마케팅 안 함": 0,
  "인스타/블로그": 200000,
  "지역 광고 포함": 600000,
  "적극 광고": 1200000,
};

// 매출 계산 함수
function calculateRequiredSales({
  coffeePrice,
  staffCost,
  marketingCost,
  ingredientCost,
  rentPerPyeong,
  pyeong,
  userCash,
  interiorCostPerPyeong,
  machineCost,
  dessertMachineCost,
  // desiredProfit 제거
}) {
  const rent = rentPerPyeong * pyeong;
  const interior = interiorCostPerPyeong * pyeong;

  const startupCost =
    rent + interior + machineCost + dessertMachineCost - userCash;

  const monthlyFixedCost = rent + staffCost + marketingCost + ingredientCost;
  const profitPerCup = coffeePrice - 1000; // 원가 1000원 가정
  const costPerCup = 1000;

  // 본전 기준: desiredProfit을 0으로 가정하여 분기점 계산
  const requiredMonthlySales = Math.ceil(monthlyFixedCost / profitPerCup);
  const requiredDailySales = Math.ceil(requiredMonthlySales / 30);
  const requiredHourlySales = Math.ceil(requiredDailySales / 10);

  const message = `월 ${requiredMonthlySales}잔 판매 시 본전 도달 (순수익 0원 기준)`;

  return {
    totalStartupCost: startupCost,
    monthlyFixedCost,
    costPerCup,
    profitPerCup,
    requiredMonthlySales,
    requiredDailySales,
    requiredHourlySales,
    message,
  };
}

// 입력값 매핑 및 계산
function mapAndCalculate(inputs, userCash = 0) {
  const rentPerPyeong = regionRentMap[inputs.location];
  const { pyeong, staffCost: sizeStaffCost } = sizeMap[inputs.size];
  const {
    machineCost,
    ingredientCost: coffeeIngredient,
    baristaCost,
  } = coffeeStrategyMap[inputs.coffee];
  const { dessertMachineCost, dessertIngredientCost, pastryCost } =
    dessertStrategyMap[inputs.dessert];
  const interiorCostPerPyeong = interiorMap[inputs.interior];
  const coffeePrice = coffeePriceMap[inputs.price];
  const extraStaffCost = manpowerMap[inputs.manpower];
  const marketingCost = marketingMap[inputs.marketing];

  const ingredientCost = coffeeIngredient + dessertIngredientCost;
  const staffCost = sizeStaffCost + baristaCost + pastryCost + extraStaffCost;

  return calculateRequiredSales({
    coffeePrice,
    staffCost,
    marketingCost,
    ingredientCost,
    rentPerPyeong,
    pyeong,
    userCash,
    interiorCostPerPyeong,
    machineCost,
    dessertMachineCost,
  });
}

let finalResult = null;

// 계산 실행 + 결과 저장 + UI 업데이트
async function submitAndSave(inputs, userCash = 0) {
  // localStorage에서 세션 ID 가져오기
  let sessionId = localStorage.getItem("session_id");

  // 없으면 createSession() 호출해서 DB에 세션 생성 및 ID 받아오기
  if (!sessionId) {
    sessionId = await createSession();
    if (!sessionId) {
      console.error("세션 생성 실패, 저장 중단");
      return; // 세션 없으면 저장 중단
    }
    localStorage.setItem("session_id", sessionId);
  }

  const result = mapAndCalculate(inputs, userCash);
  finalResult = result;

  const { error } = await supabase.from("startup_result").insert([
    {
      session_id: sessionId,
      startup_cost: result.totalStartupCost,
      monthly_fixed_cost: result.monthlyFixedCost,
      cost_per_cup: result.costPerCup,
      profit_per_cup: result.profitPerCup,
      required_monthly_sales: result.requiredMonthlySales,
      required_daily_sales: result.requiredDailySales,
      required_hourly_sales: result.requiredHourlySales,
      result_message: result.message,
    },
  ]);

  if (error) {
    console.error("Supabase 저장 오류:", error.message);
  }

  renderResult();
}

// 질문 예시 배열 (필수)
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

// UI 관련 변수 및 상태
let currentIndex = 0;
const answers = [];
const introBox = document.getElementById("introBox");
const questionBox = document.getElementById("question-box");
const resultBox = document.getElementById("result-box");
const resultContent = document.getElementById("result-content");

let userCashInput = 0;
// 질문 배열과 기존 renderQuestion, convertAnswersToInputObject, submitAndSave 함수는 그대로 유지

// 질문 렌더링 및 응답 처리
let questionStartTime = performance.now();

function renderQuestion() {
  const q = questions[currentIndex];
  questionBox.innerHTML = `
    <p>${q.text}</p>
    ${q.options
      .map(
        (opt) =>
          `<button class="answer-btn">${
            opt.label
          } (${opt.cost.toLocaleString()}원)</button>`
      )
      .join("")}
  `;

  document.querySelectorAll(".answer-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      const now = performance.now();
      const timeElapsed = Math.round(now - questionStartTime);
      questionStartTime = now;

      const answerText = btn.textContent;
      answers.push({ question_id: q.id, value: answerText });

      // 응답 저장 (응답 시간 포함)
      await saveResponse(q.id, answerText, timeElapsed);

      currentIndex++;
      if (currentIndex < questions.length) {
        renderQuestion();
      } else {
        const inputObject = convertAnswersToInputObject(answers); // ✅ 유저 응답을 계산에 적합하게 변환
        submitAndSave(inputObject, userCashInput);
      }
    });
  });
}

function convertAnswersToInputObject(answers) {
  const getAnswer = (id) =>
    answers
      .find((a) => a.question_id === id)
      ?.value?.replace(/\s*\(\d{1,3}(,\d{3})*원\)/, "")
      ?.trim();

  return {
    location: getAnswer("Q1_LOCATION"),
    size: getAnswer("Q2_SIZE"),
    coffee: getAnswer("Q3_COFFEE"),
    dessert: getAnswer("Q4_DESSERT"),
    interior: getAnswer("Q5_INTERIOR"),
    price: getAnswer("Q6_PRICE"),
    manpower: getAnswer("Q7_MANPOWER"),
    marketing: getAnswer("Q8_MARKETING"),
  };
}

// 결과 렌더링
function renderResult() {
  questionBox.classList.add("hidden");
  resultBox.classList.remove("hidden");

  const {
    requiredMonthlySales,
    requiredDailySales,
    requiredHourlySales,
    totalStartupCost,
    message,
  } = finalResult;

  const location = answers.find((a) => a.question_id === "Q1_LOCATION")?.value;
  const size = answers.find((a) => a.question_id === "Q2_SIZE")?.value;

  resultContent.innerHTML = `
    <h2>💡 창업 시뮬레이션 결과</h2>
    <p><strong>${location}</strong> 지역에서 <strong>${size}</strong> 매장으로 창업을 계획하셨군요!</p>
    <p>☕ 아메리카노를 <strong>월 ${requiredMonthlySales.toLocaleString()}잔</strong> 팔아야 해요!</p>
    <p>= 하루 ${requiredDailySales.toLocaleString()}잔 / 시간당 ${requiredHourlySales.toLocaleString()}잔 필요</p>
    <p><em>1초당 커피 머신이 식을 틈이 없어요...🔥</em></p>
    <p>총 창업비용: ${totalStartupCost.toLocaleString()}원</p>
    <p>${message}</p>
  `;
}

window.addEventListener("DOMContentLoaded", async () => {
  // 세션 생성 및 초기화
  let sessionId = localStorage.getItem("session_id");
  if (!sessionId) {
    sessionId = await createSession();
    if (!sessionId) {
      console.warn("세션 생성 실패. Supabase 연결 확인 필요.");
    }
  }

  // UI 초기화 함수 호출
  renderQuestion();

  //  이벤트 리스너 등록
  const startBtn = document.getElementById("start-btn");
  const skipBtn = document.getElementById("skip-btn");
  const retryBtn = document.getElementById("retry-btn");
  const goToLinkBtn = document.getElementById("goToLinkBtn");

  if (startBtn) {
    startBtn.addEventListener("click", () => {
      // userCashInput, currentIndex, answers 초기화 등 기존 startBtn 클릭 핸들러 내용
      userCashInput = Number(document.getElementById("cash-input").value) || 0;
      currentIndex = 0;
      answers.length = 0;
      finalResult = null;

      introBox.classList.add("hidden");
      resultBox.classList.add("hidden");
      questionBox.classList.remove("hidden");

      renderQuestion();
    });
  } else {
    console.error("startBtn 요소를 찾지 못했습니다.");
  }

  if (skipBtn) {
    // 선택하지 않고 넘어가기 버튼 클릭 시
    skipBtn.addEventListener("click", () => {
      userCashInput = 0; // 0으로 초기화
      currentIndex = 0;
      answers.length = 0;
      finalResult = null;

      introBox.classList.add("hidden");
      resultBox.classList.add("hidden");
      questionBox.classList.remove("hidden");

      renderQuestion();
    });
  } else {
    console.error("skipBtn 요소를 찾지 못했습니다.");
  }

  if (retryBtn) {
    // 다시하기 버튼 클릭 시
    retryBtn.addEventListener("click", () => {
      currentIndex = 0;
      answers.length = 0;
      finalResult = null;

      resultBox.classList.add("hidden");
      introBox.classList.remove("hidden");
    });
  } else {
    console.error("retryBtn 요소를 찾지 못했습니다.");
  }

  if (goToLinkBtn) {
    // 사이트로 이동 버튼 클릭 시
    goToLinkBtn.addEventListener("click", async () => {
      await logEvent("redirect", {
        target_url: "https://example.com", // 변경 가능
        final_choice: answers,
      });
      window.location.href = "https://example.com"; // 변경 가능
    });
  } else {
    console.error("goToLinkBtn 요소를 찾지 못했습니다.");
  }

  // (필요하다면 다른 버튼들도 같은 방식으로 여기서 이벤트 등록)
});

// 이탈 감지 강화
document.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "hidden") {
    logEvent("exit", { step: currentIndex, reason: "tab_hidden" });
  }
});
window.addEventListener("beforeunload", () => {
  logEvent("exit", { step: currentIndex, reason: "unload" });
});
