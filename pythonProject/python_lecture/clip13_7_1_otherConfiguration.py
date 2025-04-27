

conf_path = "/Users/jun/GitStudy/Data_Engineer/pythonProject/python_lecture/config_folder/file.conf"


import logging
import logging.config
import os

logging.config.fileConfig(fname=conf_path
                        ,disable_existing_loggers=False)

logger = logging.getLogger(__name__)
logger.debug("This is a debug message")


