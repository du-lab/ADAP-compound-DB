from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service

# Selenium's HTTP client defaults to a 120s read timeout when talking to
# ChromeDriver. Group search and other long-running page loads can exceed that.
_DRIVER_TIMEOUT_SECONDS = 300


def _set_http_timeout(driver, seconds: int) -> None:
    """Set Selenium's HTTP read timeout for ChromeDriver communication."""
    executor = driver.command_executor
    client_config = getattr(executor, "client_config", None) or executor._client_config
    client_config.timeout = seconds


def create_driver():
    options = Options()
    options.binary_location = (
        ".tools/chrome-for-testing/chrome-mac-arm64/"
        "Google Chrome for Testing.app/Contents/MacOS/Google Chrome for Testing"
    )
    # uncomment this to open visible browser
    options.add_argument("--headless=new")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1920,1080")

    service = Service(
        ".tools/chrome-for-testing/chromedriver-mac-arm64/chromedriver"
    )
    driver = webdriver.Chrome(service=service, options=options)
    _set_http_timeout(driver, _DRIVER_TIMEOUT_SECONDS)
    driver.set_page_load_timeout(_DRIVER_TIMEOUT_SECONDS)
    driver.set_script_timeout(_DRIVER_TIMEOUT_SECONDS)
    return driver