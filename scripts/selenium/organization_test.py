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
# generate random string as username
def random_string(stringLength):
    letters = string.ascii_letters
    return ''.join(random.choice(letters) for i in range(stringLength))

def create_account(homepage_url, username, password, organization):
    driver.get(homepage_url)
    # go to login page
    driver.find_element("id", 'loginPage').click()
    # go to registration
    driver.find_element("id", 'registerButton').click()
    #enter username, email, password, check organization
    driver.find_element("id", 'username').send_keys(username)
    driver.find_element("id", 'email').send_keys(username + '@gmail.com')
    driver.find_element("id", 'confirmedEmail').send_keys(username + '@gmail.com')
    driver.find_element("id", 'password').send_keys(password)
    driver.find_element("id", 'confirmedPassword').send_keys(password)
    if organization :
        checkbox = driver.find_element('id', "orgAcc")
        driver.execute_script("arguments[0].click();", checkbox)
    time.sleep(1)
    #submit new registration
    driver.find_element("id", 'submit').click()
    time.sleep(1)
    # login with new organization details
    driver.find_element("id", 'username').send_keys(username)
    driver.find_element("id", 'password').send_keys(password)
    driver.find_element("name", "submit").click()
    time.sleep(1)
    # go to account page
    account_page_button = driver.find_element("id", "accountPage")
    account_page_button.click()
    time.sleep(1)

    # validate if current url is account page
    assert (driver.current_url.startswith(urljoin(homepage_url, 'account/')))
    if organization:
        #validate if account is organization
        assert "Organization Account" in driver.find_element('id', 'full_username').text


def logout():
    time.sleep(1)
    driver.find_element('id','logoutPage').click();
    time.sleep(1)


def invite_user_to_organization(acc_username):
    driver.find_element('id','organizationTab').click()
    driver.find_element('id','username').send_keys(acc_username)
    driver.find_element('id','organizationSubmitButton').click()
    checkbox = driver.find_element('name', "selectedUsers")
    driver.execute_script("arguments[0].click();", checkbox)
    time.sleep(1)
    driver.find_element('id', 'organizationAdd').click();
    time.sleep(1)
    success_text= driver.find_element('id', 'organization-success').text
    assert "Invitation sent to user." in success_text

def login(homepage_url, acc_username, acc_password):
    driver.get(homepage_url)
    # go to login page
    driver.find_element("id", 'loginPage').click()
    # login with details
    driver.find_element("id", 'username').send_keys(acc_username)
    driver.find_element("id", 'password').send_keys(acc_password)
    driver.find_element("name", "submit").click()
    time.sleep(1)
    account_page_button = driver.find_element("id", "accountPage")
    account_page_button.click()
    time.sleep(1)


def add_user_to_organization(homepage_url, organization_username, token):
    time.sleep(1)
    driver.get(homepage_url+
               "/organization/addUser?"
               "token="+token+"&orgEmail="
               +organization_username)
    time.sleep(1)


def check_if_user_is_in_organization(acc_username):
    driver.find_element('id','organizationTab').click()
    time.sleep(1)
    driver.find_element(By.XPATH, "//td[contains(text(), "+acc_username+")]")


def remove_user_from_organization():
    time.sleep(1)
    # driver.find_element("id", "organization_dialog").click()
    driver.find_element(By.XPATH, "//a[contains(text(), 'Remove User')]").click()
    time.sleep(1)
    driver.find_element(By.XPATH,"//button[text()='Delete']").click()
    time.sleep(1)
    success_text= driver.find_element('id', 'organization-success').text
    assert "User deleted from organization." in success_text


def leave_organization_and_convert_to_organization():
    driver.find_element(By.XPATH, "//a[contains(text(), 'Leave Organization')]").click()
    time.sleep(1)
    driver.find_element(By.XPATH, "//a[contains(text(), 'Convert to Organization')]").click()
    time.sleep(1)
    driver.find_element('id','organizationTab').click()
    time.sleep(1)


def delete_account(homepage_url, acc_username, password):
    driver.get(homepage_url)
    # go to login page
    driver.find_element("id", 'loginPage').click()
    # login with details
    driver.find_element("id", 'username').send_keys(acc_username)
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


def check_if_account_deleted(homepage_url, acc_username, password):
    driver.get(homepage_url)
    # go to login page
    driver.find_element("id", 'loginPage').click()
    # login with details
    driver.find_element("id", 'username').send_keys(acc_username)
    driver.find_element("id", 'password').send_keys(password)
    driver.find_element("name", "submit").click()
    time.sleep(1)
    expected_text = "Please try again"
    error_msg = "Error message is not displayed"
    error_div = driver.find_element(By.XPATH, '//div[contains(text(), "'+expected_text+'")]')
    assert error_div.is_displayed(), error_msg


def main():
    """Main function that is called from a command line"""
    try:
        acc_username = random_string(8)
        organization_username = random_string(8)
        password = "Paxu0$72#0"
        parser = argparse.ArgumentParser()
        parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
        args = parser.parse_args()
        homepage_url = args.homepage_url

        # create user account
        create_account(homepage_url, acc_username, password, False)
        logout()

        # create organization account
        create_account(homepage_url, organization_username, password, True)

        # invite user to organization
        # we are setting the token to be the username of the account
        # only when integration test flag is turned on
        invite_user_to_organization(acc_username)
        logout()

        # add user to organization
        # this test is just about simulating using the token link
        # since the logic uses token and organization user's email to add to organization
        add_user_to_organization(homepage_url, organization_username+"@gmail.com", acc_username);


        login(homepage_url, organization_username, password)

        check_if_user_is_in_organization(acc_username)
        remove_user_from_organization()
        time.sleep(3)

        # invite user to organization
        # we are setting the token to be the username of the account
        # only when integration test flag is turned on
        invite_user_to_organization(acc_username)
        logout()

        # add user to organization
        # this test is just about simulating using the token link
        # since the logic uses token and organization user's email to add to organization
        add_user_to_organization(homepage_url, organization_username+"@gmail.com", acc_username);


        # login to user account
        login(homepage_url, acc_username, password)
        leave_organization_and_convert_to_organization()
        logout()

        # delete user account
        delete_account(homepage_url, acc_username, password)

        # delete organization account
        delete_account(homepage_url, organization_username, password)

        # check if both accounts are deleted
        check_if_account_deleted(homepage_url, acc_username, password)
        check_if_account_deleted(homepage_url, organization_username, password)

    except Exception as e:
        driver.quit()
        raise e


if __name__ == '__main__':
    main()
