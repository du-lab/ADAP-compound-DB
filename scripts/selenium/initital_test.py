from selenium import webdriver
from selenium.webdriver.common.keys import Keys


# Open the Chrome browser
driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

# Open a web page
driver.get("https://www.python.org")

# Print out a title of the web page
print(driver.title)

# Close the browser
driver.close()