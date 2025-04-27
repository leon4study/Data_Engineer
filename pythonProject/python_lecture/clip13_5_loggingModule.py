import os
import logging


current_dir = os.path.dirname(os.path.abspath(__file__))
log_dir = os.path.join(current_dir, "log_folder")
log_path = os.path.join(log_dir, "clip13_5.log")

logging.basicConfig(
    level="DEBUG", 
    filename= log_path, 
    filemode='w', 
    format= '%(asctime)s - %(thread)s - %(levelname)s - %(message)s',
    datefmt = '%d-%b-%y %H:%M:%S')


logger = logging.getLogger(__name__)

c_handler = logging.StreamHandler() #standard out 해주는 핸들러
f_handler = logging.FileHandler(os.path.join(log_dir, "file.log")) #파일에 로그를 남겨주는 핸들러

c_handler.setLevel(logging.WARNING)
f_handler.setLevel(logging.ERROR)

c_formatter = logging.Formatter('%(asctime)s - %(thread)s - %(levelname)s - %(message)s')
c_handler.setFormatter(c_formatter)
f_formatter = logging.Formatter('%(asctime)s - %(thread)s - %(levelname)s - %(message)s')
f_handler.setFormatter(f_formatter)

logger.addHandler(c_handler)
logger.addHandler(f_handler)

logger.info("This is info")
logger.warning("This is warning")
logger.error("This is error")

