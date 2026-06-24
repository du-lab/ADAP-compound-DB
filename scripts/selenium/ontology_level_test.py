#!/usr/bin/env python3

import argparse

from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_factory import create_driver


def ontology_level_test(homepage_url, username, password):
    driver = create_driver()
    wait = WebDriverWait(driver, 180)

    try:
        driver.get(homepage_url)
        # Login page
        wait.until(EC.element_to_be_clickable((By.ID, "loginPage"))).click()
        assert driver.current_url.endswith("login/")
        driver.find_element(By.ID, "username").send_keys(username)
        driver.find_element(By.ID, "password").send_keys(password)
        driver.find_element(By.NAME, "submit").click()
        # Account page
        wait.until(EC.element_to_be_clickable((By.ID, "accountPage"))).click()
        assert driver.current_url.endswith("account/")
        driver.find_element(By.ID, "librariesTab").click()
        wait.until(
            EC.element_to_be_clickable(
                (By.XPATH, '//div[@id="libraries"]//a[contains(@href, "/submission/")]')
            )
        ).click()
        wait.until(lambda d: "/submission/" in d.current_url)
        driver.find_element(By.ID, "searchMenu").click()
        driver.find_element(By.ID, "searchWithOntologyLevels").click()
        wait.until(lambda d: "/group_search/parameters" in d.current_url)
        driver.find_element(By.ID, "searchButton").click()

        # Search results page (group search can take several minutes)
        wait.until(lambda d: "/group_search/" in d.current_url and "/parameters" not in d.current_url)
        assert '/group_search/' in driver.current_url.__str__()
        driver.find_elements(By.XPATH, '//table[@id="match_table"]//*[text()="OL_1"]')

        # Log out
        driver.find_element(By.XPATH, '//a[@id="logoutPage"]').click()
        wait.until(lambda d: len(d.find_elements(By.ID, "accountPage")) < 1)

    finally:
        driver.quit()


if __name__ == '__main__':
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--username', help='ADAP-KDB User name', required=True)
    parser.add_argument('--password', help='ADAP-KDB User password', required=True)
    args = parser.parse_args()

    ontology_level_test(args.homepage_url, args.username, args.password)