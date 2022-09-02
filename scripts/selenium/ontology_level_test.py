#!/usr/bin/env python3

import argparse
import time

from selenium import webdriver
from selenium.webdriver.support.ui import Select
from urllib.parse import urljoin


def ontology_level_test(homepage_url, username, password):
    driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

    try:
        driver.get(homepage_url)

        # Login page
        driver.find_element_by_id('loginPage').click()
        assert driver.current_url.__str__().endswith('login/')
        driver.find_element_by_id('username').send_keys(username)
        driver.find_element_by_id('password').send_keys(password)
        driver.find_element_by_name('submit').click()

        # Account page
        driver.find_element_by_id('accountPage').click()
        assert driver.current_url.__str__().endswith('account/')
        driver.find_element_by_id('librariesTab').click()
        driver.find_element_by_xpath('//div[@id="libraries"]//a[contains(@href, "/submission/")]').click()

        # Submission page
        assert '/submission/' in driver.current_url.__str__()
        driver.find_element_by_id('searchMenu').click()
        driver.find_element_by_id('searchWithOntologyLevels').click()

        # Search parameters page
        assert '/group_search/parameters' in driver.current_url.__str__()
        driver.find_element_by_id('searchButton').click()
        time.sleep(30)

        # Search results page
        assert '/group_search/' in driver.current_url.__str__()
        driver.find_element_by_xpath('//table[@id="match_table"]//*[text()="OL_1"]')

        # Log out
        driver.find_element_by_xpath('//a[@id="logoutPage"]').click()
        assert driver.find_elements_by_id('accountPage').size() < 1

    except Exception as e:
        driver.quit()
        raise e


if __name__ == '__main__':
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--username', help='ADAP-KDB User name', required=True)
    parser.add_argument('--password', help='ADAP-KDB User password', required=True)
    args = parser.parse_args()

    ontology_level_test(args.homepage_url, args.username, args.password)