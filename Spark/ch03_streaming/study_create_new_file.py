import os

if __name__ == "__main__":
    base_dir = os.path.dirname(os.path.abspath(__file__))
    data_path = base_dir+"/data"
    stoks_file_path = data_path+"/stocks"

    for i in range(10):
        file_name = f"{stoks_file_path}/{i}.csv"


        with open(file_name, "w") as file:
            data = "AAPL,2023.4.14,166.3200073"
            file.write(data)