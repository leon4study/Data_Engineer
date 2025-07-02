import { createClient } from "https://cdn.jsdelivr.net/npm/@supabase/supabase-js/+esm";

// ✅ Supabase 연결
const supabase = createClient(
  "https://YOUR_PROJECT_ID.supabase.co",
  "YOUR_PUBLIC_ANON_KEY"
);

// ✅ 세션 ID 관리
async function createSession() {
  const userAgent = navigator.userAgent;
  const referrer = document.referrer;
  const platform = navigator.platform;

  const { data, error } = await supabase
    .from("sessions")
    .insert([{ user_agent: userAgent, referrer, platform }])
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

// 질문 응답 저장
async function saveResponse(questionCode, choiceCode, choiceIndex = null) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  await supabase.from("startup_result").insert([
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
}

//여유 자금 입력 여부 저장
function handleCashInput(userInputCash) {
  const valueToStore =
    userInputCash === null || userInputCash === ""
      ? "NOT_PROVIDED"
      : userInputCash.toString();

  saveResponse("Q1_CASH", valueToStore);
}

//이탈, 리디렉션 이벤트 저장
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

// 예: 결과 페이지에서 외부 사이트로 이동 전
document.getElementById("goToLinkBtn").addEventListener("click", () => {
  logEvent("redirect", { target_url: "https://example.com" });
  window.location.href = "https://example.com";
});

// 이탈률 측정 (optional)
window.addEventListener("beforeunload", (e) => {
  logEvent("exit", { page: window.location.pathname });
});

// 페이지 시간 추적 예시 (질문 응답 시 응답 시간 포함)
let questionStartTime = performance.now();

async function handleAnswer(questionId, answer) {
  const now = performance.now();
  const timeElapsed = Math.round(now - questionStartTime);
  questionStartTime = now;

  await saveResponse(questionId, answer, null); // timeElapsedMs가 필요한 경우 schema에 추가 필요
}

// ✅ 선택지 매핑 객체

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

//선택값을 수치로 변환하고 계산
function mapAndCalculate(inputs, userCash = 0, desiredProfit = 0) {
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

  const calcResult = calculateRequiredSales({
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
    desiredProfit,
  });

  return calcResult;
}

// 계산 실행 + Supabase 저장
async function submitAndSave(inputs, userCash = 0, desiredProfit = 0) {
  const result = mapAndCalculate(inputs, userCash, desiredProfit);
  const sessionId = localStorage.getItem("session_id") || crypto.randomUUID();
  localStorage.setItem("session_id", sessionId);

  // Supabase 저장
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

  if (error) console.error("Supabase 저장 오류:", error.message);

  // 결과 표시
  document.getElementById("result-box").innerHTML = `
    <p>${result.message}</p>
    <p>총 창업비용: ${result.totalStartupCost.toLocaleString()}원</p>
  `;
}

// 예시 사용
const inputData = {
  location: "연남동",
  size: "중형",
  coffee: "스페셜티 + 라떼아트",
  dessert: "제과사 고용",
  interior: "감성 카페",
  price: "4,800원",
  manpower: "알바 1명",
  marketing: "지역 광고 포함",
};

submitAndSave(inputData, 30000000, 2000000);

let currentIndex = 0;
const answers = [];

const questionBox = document.getElementById("question-box");
const resultBox = document.getElementById("result-box");

function renderQuestion() {
  const q = questions[currentIndex];
  questionBox.innerHTML = `
    <p>${q.text}</p>
    ${q.options
      .map((opt) => `<button class="answer-btn">${opt}</button>`)
      .join("")}
  `;

  document.querySelectorAll(".answer-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      // 로그 저장
      answers.push({ question_id: q.id, value: btn.textContent });

      await saveResponse(q.id, btn.textContent);

      currentIndex++;
      if (currentIndex < questions.length) {
        renderQuestion();
      } else {
        renderResult();
      }
    });
  });
}

function renderResult() {
  questionBox.classList.add("hidden");
  resultBox.classList.remove("hidden");

  resultBox.innerHTML = `
    <h2>결과</h2>
    <p>당신은 카페 ${answers[0].value}에서 ${answers[1].value} 창업을 계획 중입니다!</p>
  `;
}

// ✅ 최초 실행
renderQuestion();

// 세션 생성 강제 호출
window.addEventListener("DOMContentLoaded", async () => {
  const existingSession = localStorage.getItem("session_id");
  if (!existingSession) {
    const sessionId = await createSession();
    if (!sessionId) {
      console.warn("세션 생성 실패. Supabase 연결 확인 필요.");
    }
  }
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

// 외부 링크 클릭 추적
document.getElementById("goToLinkBtn").addEventListener("click", () => {
  logEvent("redirect", {
    target_url: "https://example.com",
    final_choice: answers,
  });
  window.location.href = "https://example.com";
});

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
  desiredProfit,
}) {
  const rent = rentPerPyeong * pyeong;
  const interior = interiorCostPerPyeong * pyeong;

  const startupCost =
    rent + interior + machineCost + dessertMachineCost - userCash;

  const monthlyFixedCost = rent + staffCost + marketingCost + ingredientCost;
  const profitPerCup = coffeePrice - 1000; // 가정: 원가 1000원
  const costPerCup = 1000;

  const requiredMonthlySales = Math.ceil(
    (monthlyFixedCost + desiredProfit) / profitPerCup
  );
  const requiredDailySales = Math.ceil(requiredMonthlySales / 30);
  const requiredHourlySales = Math.ceil(requiredDailySales / 10); // 10시간 영업 기준

  const message = `월 ${requiredMonthlySales}잔 판매 필요 (수익 ${desiredProfit.toLocaleString()}원 목표)`;

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
