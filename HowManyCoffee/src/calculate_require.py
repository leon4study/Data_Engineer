import math


def calculate_required_sales(
    coffee_price: int,
    staff_cost: int,
    marketing_cost: int,
    ingredient_cost: int,
    rent_per_pyeong: int,
    pyeong: int,
    user_cash: int = 0,
    interior_cost_per_pyeong: int = 0,
    machine_cost: int = 0,
    dessert_machine_cost: int = 0,
    base_cost_dict: dict = None,
    desired_profit: int = 0,  # 월 목표 이익
    operating_days: int = 30,
    operating_hours: int = 10,
):
    # --- 1. 총 창업 비용 ---
    rent_cost = rent_per_pyeong * pyeong
    interior_cost = interior_cost_per_pyeong * pyeong
    total_startup_cost = (
        rent_cost + interior_cost + machine_cost + dessert_machine_cost + marketing_cost
    ) - user_cash

    # --- 2. 월 고정비용 ---
    monthly_fixed_cost = staff_cost + marketing_cost + ingredient_cost

    # --- 3. 원가 per cup ---
    if base_cost_dict is None:
        base_cost_dict = {
            "bean": 600,
            "ice_water": 100,
            "packaging": 200,
            "labor": 320,
            "indirect": 500,
        }
    cost_per_cup = sum(base_cost_dict.values())

    # --- 4. 잔당 수익 및 목표 판매량 ---
    profit_per_cup = coffee_price - cost_per_cup
    if profit_per_cup <= 0:
        return {"error": "원가가 판매가보다 높습니다. 가격을 재조정하세요."}

    # 목표 수익 = 고정비 + 목표 이익
    target_monthly_profit = monthly_fixed_cost + desired_profit
    required_monthly_sales = math.ceil(target_monthly_profit / profit_per_cup)
    required_daily_sales = math.ceil(required_monthly_sales / operating_days)
    required_hourly_sales = math.ceil(required_daily_sales / operating_hours)

    return {
        "coffee_price": coffee_price,
        "cost_per_cup": cost_per_cup,
        "profit_per_cup": profit_per_cup,
        "monthly_fixed_cost": monthly_fixed_cost,
        "total_startup_cost": total_startup_cost,
        "required_monthly_sales": required_monthly_sales,
        "required_daily_sales": required_daily_sales,
        "required_hourly_sales": required_hourly_sales,
        "message": f"한 달에 {required_monthly_sales}잔! 하루에 {required_daily_sales}잔! 시간당 {required_hourly_sales}잔을 팔아야 본전이에요.",
    }
