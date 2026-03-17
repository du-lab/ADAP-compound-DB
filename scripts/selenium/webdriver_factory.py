from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service

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
    return webdriver.Chrome(service=service, options=options)