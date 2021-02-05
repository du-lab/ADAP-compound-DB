#!/usr/bin/env python3

"""this script is using for auto-testing individual search test after login"""
import argparse
import time

from selenium import webdriver
from selenium.webdriver.support.ui import Select


def individual_search_test(homepage_url, msp_path, user_name, user_password):
    driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

    try:
        driver.get(homepage_url)

        # go to login page
        upload_page_button = driver.find_element_by_id('loginPage')
        upload_page_button.click()

        # log in with new created user information
        username_login = driver.find_element_by_id('username')
        username_login.send_keys(user_name)

        password_login = driver.find_element_by_id('password')
        password_login.send_keys(user_password)

        driver.find_element_by_name("submit").click()

        # upload msp file
        upload_page_button = driver.find_element_by_id('uploadPage')
        upload_page_button.click()

        option_bar = Select(driver.find_element_by_id('chromatographyType'))
        choose_key = driver.find_element_by_name('files')
        submit_button = driver.find_element_by_name("submit")
        option_bar.select_by_visible_text('Gas Chromatography')
        choose_key.send_keys(msp_path)
        submit_button.click()

        # add 5 seconds delay for msp file to upload before next step
        time.sleep(5)

        # click Mass Spectra tab
        mass_spectra_tab = driver.find_element_by_id("mass_spectra_link")
        mass_spectra_tab.click()

        # choose the first spectrum and go to the spectrum page
        spectra_table = driver.find_element_by_id("spectrum_table")
        spectra_list = spectra_table.find_elements_by_css_selector("table>tbody>tr>td")
        # choose the spectrum link
        spectrum_link = spectra_list[1].find_element_by_css_selector('a')
        spectrum_link.click()

        # click search button on spectrum page
        search_button = driver.find_element_by_class_name('button')
        search_button.click()

        # click search button on single search page
#         search_button = driver.find_element_by_class_name('button')
        search_button = driver.find_element_by_id('searchButton')
        search_button.click()

        # add 10 seconds delay for spectrum search complete before next step
        time.sleep(10)

        # check if spectrum figure is plotted
        assert (driver.find_element_by_id('plot'))

        # check if the matching table contains values
        matching_table = driver.find_element_by_id('table')
        data_list = matching_table.find_elements_by_css_selector('table>tbody>tr')
        assert data_list

    except Exception as e:
        driver.quit()
        raise e


def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage_url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--msp_path', help='path for msv file', required=True)
    parser.add_argument('--user_name', help='username for login', required=True)
    parser.add_argument('--user_password', help='user password fpr login', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url
    msp_path = args.msp_path
    user_name = args.user_name
    user_password = args.user_password

    individual_search_test(homepage_url, msp_path, user_name, user_password)


if __name__ == '__main__':
    main()
