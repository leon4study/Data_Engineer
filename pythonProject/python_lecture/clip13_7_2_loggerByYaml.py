import logging
import logging.config
import yaml

yaml_path = "/Users/jun/GitStudy/Data_Engineer/pythonProject/python_lecture/config_folder/config.yaml"

with open(yaml_path, 'r') as f:
    config = yaml.safe_load(f.read())
    logging.config.dictConfig(config)

logger = logging.getLogger(__name__)
logger.debug("This is a debug message")