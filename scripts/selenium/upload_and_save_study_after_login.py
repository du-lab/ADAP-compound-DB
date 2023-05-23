import argparse

import time
from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.webdriver.common.by import By

from urllib.parse import urljoin


def upload_and_save_test(homepage_url, msp_path, user_name, user_password):
    driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

    try:
        driver.get(homepage_url)

        #log into account
        upload_page_button = driver.find_element(By.ID, 'loginPage')
        upload_page_button.click()

        username_login = driver.find_element(By.ID, 'username')
        username_login.send_keys(user_name)

        password_login = driver.find_element(By.ID, 'password')
        password_login.send_keys(user_password)

        driver.find_element(By.NAME, "submit").click()

        #upload file
        upload_page_button = driver.find_element(By.ID, 'uploadPage')
        upload_page_button.click()

        option_bar = Select(driver.find_element(By.ID, 'chromatographyType'))
        choose_key = driver.find_element(By.NAME, 'files')
        upload_button = driver.find_element(By.NAME, "submit")
        option_bar.select_by_visible_text('GC')
        choose_key.send_keys(msp_path)
        upload_button.click()

        time.sleep(5)
        driver.find_element_by_id("uploadBtn").click()
        time.sleep(2)
        #save study with default parameters
        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'file/')))

        submit_button = driver.find_element(By.ID, 'submit_button')
        submit_button.click()

        time.sleep(3)

        #check if the page is rendered correctly
        assert(driver.find_element(By.ID, "submission_view"))

        #go to account page
        account_page_button = driver.find_element(By.ID, 'accountPage')
        account_page_button.click()
        time.sleep(3)
        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'account/')))

        #go to first row of table and click delete
        delete_icon =driver.find_elements(By.XPATH, "//table[@id='study_table']/tbody/tr/td[7]/a[2]")[0]
        delete_icon.click()

        #click delete button in pop up modal
        delete_button = driver.find_elements(By.XPATH, "//button[text()='Delete']")[0]
        delete_button.click()

    except Exception as e:
        driver.quit()
        raise e

def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--msp-path', help='path for msp file', required=True)
    parser.add_argument('--user-name', help='username for adap-kdb', required=True)
    parser.add_argument('--user-password', help='password', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url
    msp_path = args.msp_path
    user_name = args.user_name
    user_password = args.user_password

    upload_and_save_test(homepage_url, msp_path, user_name, user_password)


if __name__ == '__main__':
    main()
