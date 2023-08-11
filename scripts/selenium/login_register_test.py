#!/usr/bin/env python3

"""this script is using for auto-testing login and register"""
import argparse
import random
import string
from selenium import webdriver
from urllib.parse import urljoin
import time
from selenium.webdriver.common.by import By

driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

#  generate random string as username
def random_string(stringLength):
    letters = string.ascii_letters
    return ''.join(random.choice(letters) for i in range(stringLength))

# generate random password match adap-kdb required rule
def random_password(stringLength):
    special_characters = '@#$%^&+='
    password_required_characters = random.choice(special_characters) + random.choice(string.ascii_uppercase)\
                                   + random.choice(string.ascii_lowercase) + random.choice(string.digits)
    password_characters = string.ascii_lowercase + string.ascii_uppercase + string.digits + special_characters
    return ''.join(random.choice(password_characters) for i in range(stringLength-4)) + password_required_characters


def login_register_test(homepage_url, username_value, password_value):
    try:
        driver.get(homepage_url)

        # go to login page
        upload_page_button = driver.find_element("id", 'loginPage')
        upload_page_button.click()

        # go to registration
        register_button = driver.find_element("id", 'registerButton')
        register_button.click()
        time.sleep(1)
        # input username, email, password for new register user
        username_input = driver.find_element("id", 'username')
        username_input.send_keys(username_value)

        email_input = driver.find_element("id", 'email')
        email = random_string(8) + '@gmail.com'
        email_input.send_keys(email)

        confirm_email_input = driver.find_element("id", 'confirmedEmail')
        confirm_email_input.send_keys(email)

        password_input = driver.find_element("id", 'password')
        password_input.send_keys(password_value)

        confirmed_password_input = driver.find_element("id", 'confirmedPassword')
        confirmed_password_input.send_keys(password_value)
        time.sleep(1)
        # commit new registration
        submit_button = driver.find_element("id", 'submit')
        submit_button.click()
        time.sleep(1)
        # log in with new created user information
        username_login = driver.find_element("id", 'username')
        username_login.send_keys(username_value)

        password_login = driver.find_element("id", 'password')
        password_login.send_keys(password_value)
        time.sleep(1)
        driver.find_element("name", "submit").click()
        time.sleep(1)
        # go to account page
        account_page_button = driver.find_element("id", "accountPage")
        account_page_button.click()

        # validate if current url is account page
        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'account/')))
    except Exception as e:
        driver.quit()
        raise e


def delete_account(homepage_url, username, password):
    try:
        driver.get(homepage_url)
        # go to login page
        driver.find_element("id", 'loginPage').click()
        time.sleep(5)
        # login with details
        driver.find_element("id", 'username').send_keys(username)
        driver.find_element("id", 'password').send_keys(password)
        driver.find_element("name", "submit").click()
        time.sleep(1)
        account_page_button = driver.find_element("id", "accountPage")
        account_page_button.click()
        time.sleep(1)
        driver.find_element(By.XPATH, "//a[contains(text(), 'Delete Account')]").click()
        time.sleep(1)
        driver.find_element(By.XPATH, "//button[text()='Delete']").click()
        time.sleep(1)
    except Exception as e:
        driver.quit()
        raise e

def logout():
    time.sleep(1)
    driver.find_element('id','logoutPage').click();
    time.sleep(1)

def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url
    username_value = random_string(8)
    password_value = random_password(9)
    login_register_test(homepage_url, username_value, password_value)
    logout()
    delete_account(homepage_url, username_value, password_value)


if __name__ == '__main__':
    main()
