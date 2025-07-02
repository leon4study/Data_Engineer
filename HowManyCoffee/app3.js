/// events
/// question_timings
/// responses
/// sessions
/// startup_result

import { createClient } from "https://cdn.jsdelivr.net/npm/@supabase/supabase-js/+esm";

const supabaseUrl = "https://igsacelpffgejmquacci.supabase.co";
const supabaseAnonKey =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlnc2FjZWxwZmZnZWptcXVhY2NpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTEyNjc0NDYsImV4cCI6MjA2Njg0MzQ0Nn0.WgwpS1ANNk_Nr0GyKmplzJh51DLMi-au19sAFu4cMn0";

const supabase = createClient(supabaseUrl, supabaseAnonKey);

let sessionId = null;
let currentIndex = 0;
let userCashInput = 0;
let questionStartTime = performance.now();
const answers = [];
let finalResult = null;

// 선택지 매핑 객체 (생략, 이전과 동일)
const regionRentMap = {
  연남동: 150000,
  여의도: 130000,
  남양주: 50000,
  "가평 리버뷰": 40000,
  강남: 200000,
};

const sizeMap = {
  소형: { pyeong: 10, staffCost: 15 },
  중형: { pyeong: 30, staffCost: 30 },
  대형: { pyeong: 100, staffCost: 50 },
  테이크아웃: { pyeong: 5, staffCost: 5 },
};

const coffeeStrategyMap = {
  "기본형 (중고 에스프레소 머신 + 저가 원두)": {
    machineCost: 4000000, // 중고 머신
    ingredientCost: 500000, // 저가 원두 초기 계약금
  },
  "고급형 (신형 에스프레소 머신 + 프리미엄 원두)": {
    machineCost: 8000000, // 신형 머신
    ingredientCost: 500000, // 프리미엄 원두
  },
  "스페셜티 (신형 머신 + 싱글 오리진 원두)": {
    machineCost: 10000000, // 고급 머신
    ingredientCost: 1500000, // 고급 싱글 오리진 원두
  },
  "직접 로스팅 (로스터기 포함 + 생두)": {
    machineCost: 15000000, // 로스터기 + 머신
    ingredientCost: 3000000, // 생두 포함 초기 비용
  },
};

const machineCostMap = {
  기본형: 3000000,
  고급형: 6000000,
  "직접 로스팅": 15000000,
  스페셜티: 10000000,
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
  "안 함": 0,
  "+ 인스타/블로그": 200000,
  "+ 지역 광고": 600000,
  "+ 적극 홍보": 1200000,
};

const questions = [
  {
    id: "Q1_LOCATION",
    text: "카페를 열 지역은 어디인가요?",
    options: [
      { label: "연남동", cost: 150000 },
      { label: "여의도", cost: 130000 },
      { label: "남양주", cost: 50000 },
      { label: "가평", cost: 40000 },
      { label: "강남", cost: 200000 },
    ],
  },
  {
    id: "Q2_SIZE",
    text: "매장 규모를 선택해주세요. (단위: 평)",
    options: [
      { label: "소형", cost: 15 },
      { label: "중형", cost: 30 },
      { label: "대형", cost: 50 },
      { label: "테이크아웃", cost: 5 },
    ],
  },
  {
    id: "Q3_COFFEE",
    text: "커피 옵션을 선택해주세요.",
    options: [
      { label: "기본형 (중고 에스프레소 머신 + 저가 원두)", cost: 4500000 }, // 머신 400만 + 원두 계약금 또는 초기 50만
      { label: "고급형 (신형 에스프레소 머신 + 프리미엄 원두)", cost: 8500000 }, // 머신 800만 + 원두 50만
      { label: "스페셜티 (신형 머신 + 싱글 오리진 원두)", cost: 11500000 }, // 머신 1000만 + 원두 150만
      { label: "직접 로스팅 (로스터기 포함 + 생두)", cost: 18000000 }, // 로스터기 1500만 + 생두 300만
    ],
  },
  {
    id: "Q4_DESSERT",
    text: "디저트 전략은 어떻게 할까요? (재료+인건비 포함)",
    options: [
      { label: "없음", cost: 0 },
      { label: "납품 디저트", cost: 1000000 },
      { label: "제과사 고용", cost: 7700000 },
      { label: "냉동 디저트 + 데코", cost: 1400000 },
    ],
  },
  {
    id: "Q5_INTERIOR",
    text: "인테리어 스타일은? (단위: 평당 비용)",
    options: [
      { label: "셀프 인테리어", cost: 300000 },
      { label: "일반 인테리어", cost: 1000000 },
      { label: "감성 카페", cost: 2000000 },
      { label: "SNS 맛집", cost: 3000000 },
    ],
  },
  {
    id: "Q6_PRICE",
    text: "커피 1잔 가격을 선택해주세요. (소비자가 기준)",
    options: [
      { label: "4,000", cost: 4000 },
      { label: "4,800", cost: 4800 },
      { label: "6,000", cost: 6000 },
      { label: "7,500", cost: 7500 },
    ],
  },
  {
    id: "Q7_MANPOWER",
    text: "직원 구성은 어떻게 할까요? (단위: 월 인건비)",
    options: [
      { label: "혼자", cost: 0 },
      { label: "알바 1", cost: 1070000 },
      { label: "알바 2", cost: 2140000 },
      { label: "알바 3", cost: 3210000 },
    ],
  },
  {
    id: "Q8_MARKETING",
    text: "홍보 전략은 어떻게 하시겠어요? (단위: 월 광고비)",
    options: [
      { label: "안 함", cost: 0 },
      { label: "+ 인스타/블로그", cost: 200000 },
      { label: "+ 지역 광고", cost: 600000 },
      { label: "+ 적극 홍보", cost: 1200000 },
    ],
  },
  {
    id: "Q9_TARGET_PERIOD",
    text: "목표 회수 기간을 선택해주세요. (단위: 개월)",
    options: [
      { label: "1년", value: 12 },
      { label: "2년", value: 24 },
      { label: "3년", value: 36 },
      { label: "5년", value: 60 },
      { label: "6년+", value: 72 },
    ],
  },
];

const introBox = document.getElementById("introBox");
const questionBox = document.getElementById("question-box");
const resultBox = document.getElementById("result-box");
const resultContent = document.getElementById("result-content");

// 세션 생성
async function createSession(isRetry = false) {
  const { data, error } = await supabase
    .from("sessions")
    .insert([
      {
        user_agent: navigator.userAgent,
        referrer: document.referrer,
        has_capital_input: false,
        capital_amount: 0,
        ended_at: null, // 새 세션이므로 null
        created_at: new Date().toISOString(),
      },
    ])
    .select()
    .single();

  if (error || !data) {
    console.error("❌ 세션 생성 실패:", error);
    return null;
  }
  return data.session_id;
}

// 2. 세션 업데이트 (예: 자본금 입력 상태 및 금액 업데이트)
async function updateSessionCapitalInfo(hasCapitalInput, capitalAmount) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase
    .from("sessions")
    .update({
      has_capital_input: hasCapitalInput,
      capital_amount: capitalAmount,
    })
    .eq("session_id", sessionId);

  if (error) {
    console.error("세션 업데이트 오류:", error);
  }
}

// 3. 세션 종료 처리 함수 (페이지 떠날 때 호출)
async function endSession() {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase
    .from("sessions")
    .update({
      ended_at: new Date().toISOString(),
    })
    .eq("session_id", sessionId);

  if (error) {
    console.error("세션 종료 업데이트 오류:", error);
  }
}

// 이벤트 로깅
async function logEvent(eventType, metadata = {}) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase.from("events").insert([
    {
      session_id: sessionId,
      event_type: eventType,
      metadata, // JSON 형태로 저장 가능
      created_at: new Date().toISOString(),
    },
  ]);

  if (error) {
    console.error("이벤트 기록 오류:", error);
  }
}

// 질문 보기 시간 저장
async function renderQuestion() {
  const q = questions[currentIndex];
  questionBox.innerHTML = `
  <p>${q.text}</p>
  ${q.options
    .map((opt, idx) => {
      const dataValue = opt.value !== undefined ? opt.value : opt.cost;

      const displayLabel =
        q.id === "Q2_SIZE"
          ? `${opt.label} (${sizeMap[opt.label].pyeong}평)`
          : opt.label + (opt.cost ? ` (${opt.cost.toLocaleString()}원)` : "");

      return `<button class="answer-btn" data-index="${idx}" data-value="${dataValue}">${displayLabel}</button>`;
    })
    .join("")}
`;

  // 질문 보기 시간 기록
  await logQuestionView(q.id);
  questionStartTime = performance.now();

  document.querySelectorAll(".answer-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      const timeTakenMs = Math.round(performance.now() - questionStartTime);
      const choiceIndex = Number(btn.dataset.index);
      const choiceCode = q.options[choiceIndex].label;
      const choiceValue = btn.dataset.value;

      // Q9만 value 저장, 나머진 label 저장
      const answerValue =
        q.id === "Q9_TARGET_PERIOD" ? choiceValue : choiceCode;

      await saveAnswerResponse(q.id, answerValue, choiceIndex, timeTakenMs);

      await updateQuestionTiming(q.id, timeTakenMs);

      answers.push({ question_id: q.id, value: answerValue });
      currentIndex++;
      if (currentIndex < questions.length) {
        renderQuestion();
      } else {
        submitAndSave(convertAnswersToInputObject(answers), userCashInput);
      }
    });
  });
}

async function saveFinalResult(resultObj) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const {
    totalStartupCost,
    monthlyFixedCost,
    costPerCup,
    profitPerCup,
    requiredMonthlySales,
    requiredDailySales,
    requiredHourlySales,
    message,
  } = resultObj;

  const { error } = await supabase.from("startup_result").insert([
    {
      session_id: sessionId,
      startup_cost: totalStartupCost,
      monthly_fixed_cost: monthlyFixedCost,
      cost_per_cup: costPerCup,
      profit_per_cup: profitPerCup,
      required_monthly_sales: requiredMonthlySales,
      required_daily_sales: requiredDailySales,
      required_hourly_sales: requiredHourlySales,
      result_message: message,
      created_at: new Date().toISOString(),
    },
  ]);

  if (error) {
    console.error("최종 결과 저장 오류:", error);
  }
}

async function markSessionCompleted() {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase
    .from("sessions")
    .update({ completed_at: new Date().toISOString() })
    .eq("session_id", sessionId);

  if (error) {
    console.error("세션 완료 시간 업데이트 오류:", error);
  }
}

async function logQuestionView(questionCode) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const payload = {
    session_id: sessionId,
    question_code: questionCode,
    viewed_at: new Date().toISOString(),
    answered_at: null,
    time_taken_ms: null,
  };

  const { error } = await supabase.from("question_timings").insert([payload]);

  if (error) {
    console.error("❌ [409 오류 발생 payload]", payload);
    console.error("❌ [409 오류 상세 정보]", error);
  }
}

async function saveAnswerResponse(
  questionCode,
  choiceCode,
  choiceIndex,
  timeTakenMs
) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase.from("responses").insert([
    {
      session_id: sessionId,
      question_code: questionCode,
      choice_code: choiceCode,
      choice_index: choiceIndex,
      time_taken_ms: timeTakenMs,
    },
  ]);

  if (error) {
    console.error("답변 저장 오류:", error);
  }

  // saveAnswerResponse 내
  console.log(
    "saveAnswerResponse 호출:",
    questionCode,
    choiceCode,
    choiceIndex,
    timeTakenMs
  );
}

async function updateQuestionTiming(questionCode, timeTakenMs) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;

  const { error } = await supabase
    .from("question_timings")
    .update({
      answered_at: new Date().toISOString(),
      time_taken_ms: timeTakenMs,
    })
    .eq("session_id", sessionId)
    .eq("question_code", questionCode);

  if (error) {
    console.error("질문 타이밍 업데이트 오류:", error);
  }
}

function convertAnswersToInputObject(answers) {
  const getAnswer = (id) => {
    const rawValue = answers.find((a) => a.question_id === id)?.value;
    if (!rawValue) return null;

    if (id === "Q6_PRICE") {
      // 가격 항목에만 괄호 제거 후 trim
      return rawValue.replace(/\s*\(.*\)/, "").trim();
    }

    // 그 외 항목은 그대로 trim만
    return rawValue.trim();
  };

  // Q9는 value가 필요해서 별도 처리
  const getAnswerValue = (id) =>
    answers.find((a) => a.question_id === id)?.value;

  return {
    location: getAnswer("Q1_LOCATION"),
    size: getAnswer("Q2_SIZE"),
    coffee: getAnswer("Q3_COFFEE"),
    dessert: getAnswer("Q4_DESSERT"),
    interior: getAnswer("Q5_INTERIOR"),
    price: getAnswer("Q6_PRICE"),
    manpower: getAnswer("Q7_MANPOWER"),
    marketing: getAnswer("Q8_MARKETING"),
    targetPeriod: getAnswerValue("Q9_TARGET_PERIOD"),
  };
}

async function submitAndSave(inputs, userCash = 0) {
  const result = mapAndCalculate(inputs, userCash);
  await saveAndRender(result);
}

async function saveAndRender(result) {
  finalResult = result;

  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) {
    console.error("세션 ID가 없습니다.");
    return;
  }

  // 결과 저장 (created_at 추가)
  const { error: insertError } = await supabase.from("startup_result").insert([
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
      created_at: new Date().toISOString(), // created_at 필수
    },
  ]);
  if (insertError) {
    console.error("결과 저장 중 오류:", insertError);
  }

  // 세션 완료 시각 업데이트
  await markSessionCompleted();
  renderResult();
}

function renderResult() {
  questionBox.classList.add("hidden");
  resultBox.classList.remove("hidden");

  if (!finalResult) {
    console.error("finalResult가 정의되지 않았습니다.");
    resultContent.innerHTML = "<p>결과를 불러오는 데 실패했습니다.</p>";
    return;
  }

  const {
    requiredMonthlySales,
    requiredDailySales,
    requiredHourlySales,
    totalStartupCost,
    monthlyFixedCost,
    costPerCup,
    profitPerCup,
    message,
  } = finalResult;

  const location =
    answers.find((a) => a.question_id === "Q1_LOCATION")?.value || "알 수 없음";
  const size =
    answers.find((a) => a.question_id === "Q2_SIZE")?.value || "알 수 없음";

  const monthlyProfit = profitPerCup * requiredMonthlySales - monthlyFixedCost;

  const monthsToBreakEven =
    monthlyProfit > 0 ? Math.ceil(totalStartupCost / monthlyProfit) : Infinity;

  const shockMessage = generateMessage(requiredDailySales, monthsToBreakEven);

  const profitMessage =
    monthlyProfit > 0
      ? `하지만, 이게 끝이 아니죠. 매월 겨우 ${monthlyProfit.toLocaleString()}원 벌어가며 알바생 피로도는 최고조입니다.`
      : `아직은 순이익 없이 지옥 같은 매출 경쟁만 남았습니다.`;

  const secondsPerDay = 12 * 60 * 60;
  const cupsPerSecond = requiredDailySales / secondsPerDay;

  const perSecondText =
    cupsPerSecond >= 1
      ? `<p>= <strong>초당 ${cupsPerSecond.toFixed(1)}잔</strong> 판매 필요</p>`
      : "";

  resultContent.innerHTML = `
    <h2>🔥 충격 창업 시뮬레이션 결과 🔥</h2>
    <p><strong>${location}</strong>에서 <strong>${size}</strong> 매장 창업 시</p>
    <p>☕ ${shockMessage}</p>
    ${perSecondText}
    <p>${profitMessage}</p>
    <p>총 창업비용: ${totalStartupCost.toLocaleString()}원 (여기서도 이미 지쳤을 겁니다)</p>
    <p><em>※ 현실은 상상 이상입니다. 각오 단단히 하세요.</em></p>
  `;
}

async function handleStart(isSkip = false) {
  userCashInput = isSkip
    ? 0
    : Number(document.getElementById("cash-input").value) || 0;
  const sessionId = localStorage.getItem("session_id");
  await supabase
    .from("sessions")
    .update({
      has_capital_input: !isSkip,
      capital_amount: userCashInput,
    })
    .eq("session_id", sessionId);
  currentIndex = 0;
  answers.length = 0;
  finalResult = null;
  introBox.classList.add("hidden");
  resultBox.classList.add("hidden");
  questionBox.classList.remove("hidden");
  renderQuestion();
}

window.addEventListener("DOMContentLoaded", async () => {
  sessionId = localStorage.getItem("session_id");

  // localStorage에 있지만 실제 DB에 없을 가능성 있으므로 검증 필요
  if (sessionId) {
    const { data, error } = await supabase
      .from("sessions")
      .select("session_id")
      .eq("session_id", sessionId)
      .single();

    if (error || !data) {
      console.warn("⚠️ localStorage session_id 무효. 새로 생성합니다.");
      localStorage.removeItem("session_id");
      sessionId = null;
    }
  }

  if (!sessionId) {
    sessionId = await createSession();
    if (!sessionId) {
      console.error("세션 생성 실패로 앱 실행 중단");
      return;
    }
    localStorage.setItem("session_id", sessionId);
  }

  console.log("현재 세션 ID:", sessionId);
  renderQuestion();

  document
    .getElementById("start-btn")
    ?.addEventListener("click", () => handleStart(false));
  document
    .getElementById("skip-btn")
    ?.addEventListener("click", () => handleStart(true));
  document.getElementById("retry-btn")?.addEventListener("click", async () => {
    sessionId = await createSession(true);
    localStorage.setItem("session_id", sessionId);
    currentIndex = 0;
    answers.length = 0;
    finalResult = null;
    resultBox.classList.add("hidden");
    introBox.classList.remove("hidden");
  });
  document
    .getElementById("goToLinkBtn")
    ?.addEventListener("click", async () => {
      await logEvent("redirect", {
        target_url: "https://example.com",
        final_choice: answers,
      });
      window.location.href = "https://example.com";
    });
});

document.addEventListener("visibilitychange", async () => {
  if (document.visibilityState === "hidden") {
    try {
      await endSession();
      await logEvent("exit", { step: currentIndex, reason: "tab_hidden" });
    } catch (e) {
      console.warn("세션 종료 실패:", e);
    }
  }
});

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

  const targetMonths = Number(inputs.targetPeriod) || 36;

  return calculateSalesForTargetMonths({
    coffeePrice,
    staffCost,
    marketingCost,
    rentPerPyeong,
    pyeong,
    userCash,
    interiorCostPerPyeong,
    machineCost,
    dessertMachineCost,
    targetMonths,
  });
}

// 매출 계산 함수
function calculateSalesForTargetMonths({
  coffeePrice,
  staffCost,
  marketingCost,
  rentPerPyeong,
  pyeong,
  userCash,
  interiorCostPerPyeong,
  dessertMachineCost,
  machineMonthlyCost,
  ingredientCost = 0,
  targetMonths = 36,
}) {
  const rent = rentPerPyeong * pyeong;
  const interior = interiorCostPerPyeong * pyeong;

  // 초기 투자금은 커피머신 제외한 다른 고정 자산만 계산
  const startupCost = rent + interior + dessertMachineCost - userCash;

  // 월 고정비용에 머신 감가상각, 재료비 포함
  const monthlyFixedCost =
    rent + staffCost + marketingCost + machineMonthlyCost + ingredientCost;

  const costPerCup = 1720; // 잔당 원가(재료비 제외시 다시 조정 가능)
  const profitPerCup = coffeePrice - costPerCup;

  const requiredMonthlySales = Math.ceil(
    (startupCost / targetMonths + monthlyFixedCost) / profitPerCup
  );
  const requiredDailySales = Math.ceil(requiredMonthlySales / 30);
  const requiredHourlySales = Math.ceil(requiredDailySales / 10);

  const message = `목표 기간 ${targetMonths}개월 내 본전 도달을 위해 월 ${requiredMonthlySales.toLocaleString()}잔 이상 팔아야 합니다.`;

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

function generateMessage(requiredDailySales, monthsToBreakEven) {
  const yearsToBreakEven = Math.round(monthsToBreakEven / 12);

  if (requiredDailySales >= 1000) {
    return `하루 ${requiredDailySales.toLocaleString()}잔씩 팔아도 본전까지 ${yearsToBreakEven}년 걸립니다.`;
  } else if (requiredDailySales >= 300) {
    return `하루 팔아야 하는 아메리카노는 ${requiredDailySales.toLocaleString()}잔!! 2년 가까이 팔아야 본전입니다...`;
  } else if (requiredDailySales >= 100) {
    return `하루 ${requiredDailySales.toLocaleString()}잔, 약 ${monthsToBreakEven}개월은 버텨야 합니다.`;
  } else if (monthsToBreakEven > 60) {
    return `${monthsToBreakEven}개월(약 ${yearsToBreakEven}년) 팔아야 본전입니다.`;
  } else {
    return `하루 ${requiredDailySales.toLocaleString()}잔씩, ${monthsToBreakEven}개월 팔아야 합니다.`;
  }
}
