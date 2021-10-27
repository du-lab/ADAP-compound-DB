from selenium import webdriver
from selenium.webdriver.common.keys import Keys


# Open the Chrome browser
driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver.exe')

# Open a web page
driver.get("http://localhost:8080/adap_compound_db_war/")

# Print out a title of the web page
print(driver.title)

# Close the browser
driver.quit()

print('Test is complete')
