#!/usr/bin/env python3

"""this script is using for auto-testing group search without login"""
import argparse
from urllib.parse import urljoin

from selenium import webdriver
from selenium.webdriver.support.ui import Select
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService
def group_search_test(homepage_url, msp_path):
    driver = webdriver.Chrome(service=ChromeService('scripts/selenium/drivers/chromedriver'))


    try:
        driver.get(homepage_url)

        # upload msp file
        upload_page_button = driver.find_element('id','uploadPage')
        upload_page_button.click()

        option_bar = Select(driver.find_element('id','chromatographyType'))
        choose_key = driver.find_element('name','files')
        submit_button = driver.find_element('name',"submit")
        option_bar.select_by_visible_text('GC')
        choose_key.send_keys(msp_path)
        submit_button.click()

        # add 5 seconds delay for msp file to upload before next step
        time.sleep(5)
        driver.find_element('id',"uploadBtn").click()
        time.sleep(2)

        # choose the first spectrum and go to the spectrum page
        search_menu = driver.find_element('id','searchMenu')
        search_menu.click()
        time.sleep(5)

        search_parameters_button = driver.find_element('id','searchAllSpectra')
        search_parameters_button.click()

        search_button = driver.find_element('id','searchButton')
        search_button.click()

        # add 60 seconds
        time.sleep(60)

        # check if current page is file/group_search/
        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'group_search/')))

        # check if the matching table contains values
        matching_table = driver.find_element('id','match_table')
        data_list = matching_table.find_elements(By.CSS_SELECTOR, 'table>tbody>tr')
        assert data_list

        # check the search button on the file/group_search/ page
#         search_button_list = driver.find_elements_by_link_text('Search')
#         search_button_list[0].click()
        search_button = driver.find_element(By.XPATH, "//i[@title='Search spectrum']/parent::a")
        search_button.click()
        time.sleep(5)
        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'file/')))


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

    group_search_test(homepage_url, msp_path)


if __name__ == '__main__':
    main()
