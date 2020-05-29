#!/usr/bin/env python3

"""this script is using for auto-testing login and register"""
import argparse
import random
import string
from selenium import webdriver
from urllib.parse import urljoin

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


def login_register_test(homepage_url):
    driver = webdriver.Chrome('drivers/chromedriver')

    driver.get(homepage_url)

    upload_page_button = driver.find_element_by_id('loginPage')
    upload_page_button.click()

    register_button = driver.find_element_by_class_name('button')
    register_button.click()

    username_input = driver.find_element_by_id('username')
    username_value = random_string(8)
    username_input.send_keys(username_value)

    email_input = driver.find_element_by_id('email')
    email = random_string(8) + '@gmail.com'
    email_input.send_keys(email)

    confirm_email_input = driver.find_element_by_id('confirmedEmail')
    confirm_email_input.send_keys(email)

    password_input = driver.find_element_by_id('password')
    password_value = random_password(9)
    password_input.send_keys(password_value)

    confirmed_password_input = driver.find_element_by_id('confirmedPassword')
    confirmed_password_input.send_keys(password_value)

    submit_button = driver.find_element_by_id('submit')
    submit_button.click()

    username_login = driver.find_element_by_id('username')
    username_login.send_keys(username_value)

    password_login = driver.find_element_by_id('password')
    password_login.send_keys(password_value)

    driver.find_element_by_name("submit").click()

    account_page_button = driver.find_element_by_id("accountPage")
    account_page_button.click()

    assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'account/')))

    driver.quit()


def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage_url', help='url for adap-kdb homepage', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url

    login_register_test(homepage_url)


if __name__ == '__main__':
    main()
