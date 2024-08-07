#!/usr/bin/env python3

"""this script is using for auto-testing individual search without login"""
import argparse

from selenium import webdriver
from selenium.webdriver.support.ui import Select
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService

def individual_search_test(homepage_url, msp_path):
    driver = webdriver.Chrome(service=ChromeService('scripts/selenium/drivers/chromedriver'))

    try:
        driver.get(homepage_url)

        # upload msp file
        upload_page_button = driver.find_element('id', 'uploadPage')
        upload_page_button.click()

        option_bar = Select(driver.find_element('id','chromatographyType'))
        choose_key = driver.find_element('name','files')
        submit_button = driver.find_element('name',"submit")
        option_bar.select_by_visible_text('GC')
        choose_key.send_keys(msp_path)
        submit_button.click()
        # add 5 seconds delay for msp file to upload before next step
        time.sleep(5)
        driver.find_element('id', "uploadBtn").click()
        time.sleep(2)

        # Select the Data tab
        driver.find_element('id', 'mass_spectra_link').click()

        # choose the first spectrum and go to the spectrum page
        spectra_table = driver.find_element('id',"spectrum_table")
        spectra_list = spectra_table.find_elements(By.CSS_SELECTOR, "table>tbody>tr>td")
        # choose the spectrum link
        spectrum_link = spectra_list[1].find_element(By.CSS_SELECTOR, 'a')
        spectrum_link.click()

        # click search button on spectrum page
        search_button = driver.find_element('id','searchButton')
        search_button.click()

        # click search button on single search page
#         search_button = driver.find_element_by_class_name('button')
        search_button = driver.find_element('id','searchButton')
        search_button.click()

        # add 10 seconds delay for spectrum search complete before next step
        time.sleep(30)

        # check if spectrum figure is plotted
        assert (driver.find_element('id','plot'))

        # check if the matching table contains values
        matching_table = driver.find_element('id','table')
        data_list = matching_table.find_element('css selector', 'table>tbody>tr')
        assert data_list

    except Exception as e:
        driver.quit()
        raise e


def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--msp-path', help='path for msv file', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url
    msp_path = args.msp_path

    individual_search_test(homepage_url, msp_path)


if __name__ == '__main__':
    main()
