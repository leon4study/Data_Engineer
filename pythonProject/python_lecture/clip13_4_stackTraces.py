import os
import logging


current_dir = os.path.dirname(os.path.abspath(__file__))
log_path = os.path.join(current_dir, "log_folder/clip13_4.log")

logging.basicConfig(
    level="DEBUG", 
    filename= log_path, 
    filemode='w', 
    format= '%(asctime)s, %(name)s - %(levelname)s - %(message)s',
    datefmt = '%d-%b-%y %H:%M:%S')

logging.debug("This is logging")
logging.info("This is logging")
logging.warning("This is logging")
logging.error("This is logging")
logging.critical("This is logging")

a = 5
b = 0

try:
    c = a / b
except Exception as e:
    logging.error("Exception Occurred! by .error", exc_info = True)

