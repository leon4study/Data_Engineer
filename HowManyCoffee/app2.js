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
        is_retry: isRetry,
        created_at: new Date().toISOString(),
      },
    ])
    .select()
    .single();
  if (error) return null;
  localStorage.setItem("session_id", data.session_id);
  return data.session_id;
}

// 이벤트 로깅
async function logEvent(eventType, metadata = {}) {
  const sessionId = localStorage.getItem("session_id");
  if (!sessionId) return;
  await supabase
    .from("events")
    .insert([{ session_id: sessionId, event_type: eventType, metadata }]);
}

// 응답 시간 저장
async function saveResponse(qCode, choice, ms) {
  const sessionId = localStorage.getItem("session_id");
  await supabase
    .from("responses")
    .insert([
      {
        session_id: sessionId,
        question_code: qCode,
        choice_code: choice,
        choice_index: ms,
      },
    ]);
  await supabase
    .from("question_timings")
    .update({
      answered_at: new Date().toISOString(),
      time_taken_ms: ms,
    })
    .eq("session_id", sessionId)
    .eq("question_code", qCode);
}

// 질문 보기 시간 저장
async function renderQuestion() {
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
  await supabase
    .from("question_timings")
    .insert([
      {
        session_id: localStorage.getItem("session_id"),
        question_code: q.id,
        viewed_at: new Date().toISOString(),
      },
    ]);
  questionStartTime = performance.now();
  document.querySelectorAll(".answer-btn").forEach((btn) => {
    btn.addEventListener("click", async () => {
      const ms = Math.round(performance.now() - questionStartTime);
      const answerText = btn.textContent;
      answers.push({ question_id: q.id, value: answerText });
      await saveResponse(q.id, answerText, ms);
      currentIndex++;
      currentIndex < questions.length
        ? renderQuestion()
        : submitAndSave(convertAnswersToInputObject(answers), userCashInput);
    });
  });
}

function convertAnswersToInputObject(answers) {
  const getAnswer = (id) =>
    answers
      .find((a) => a.question_id === id)
      ?.value?.replace(/\s*\(\d{1,3}(,\d{3})*원\)/, "")
      .trim();
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

async function submitAndSave(inputs, userCash = 0) {
  const result = mapAndCalculate(inputs, userCash);
  finalResult = result;
  await supabase.from("startup_result").insert([
    {
      session_id: localStorage.getItem("session_id"),
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
  await supabase
    .from("sessions")
    .update({ completed_at: new Date().toISOString() })
    .eq("session_id", localStorage.getItem("session_id"));
  renderResult();
}

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
  sessionId = localStorage.getItem("session_id") || (await createSession());
  localStorage.setItem("session_id", sessionId);
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

document.addEventListener("visibilitychange", () => {
  if (document.visibilityState === "hidden") {
    logEvent("exit", { step: currentIndex, reason: "tab_hidden" });
  }
});

window.addEventListener("beforeunload", async () => {
  const sessionId = localStorage.getItem("session_id");
  if (sessionId) {
    await supabase
      .from("sessions")
      .update({ ended_at: new Date().toISOString() })
      .eq("session_id", sessionId);
    logEvent("exit", { step: currentIndex, reason: "unload" });
  }
});
