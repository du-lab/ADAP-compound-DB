#!/usr/bin/env python3

"""this script is using for auto-testing upload msp process"""
import argparse

from selenium import webdriver
from selenium.webdriver.support.ui import Select
from urllib.parse import urljoin


def upload_process_test(homepage_url, msp_path):
    driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

    try:
        driver.get(homepage_url)

        upload_page_button = driver.find_element_by_id('uploadPage')
        upload_page_button.click()

        option_bar = Select(driver.find_element_by_id('chromatographyType'))
        choose_key = driver.find_element_by_name('files')
        submit_button = driver.find_element_by_name("submit")
        option_bar.select_by_visible_text('Gas Chromatography')
        choose_key.send_keys(msp_path)
        submit_button.click()

        assert (driver.current_url.__str__().startswith(urljoin(homepage_url, 'file/')))

    except Exception as e:
        driver.quit()
        raise e

def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage_url', help='url for adap-kdb homepage', required=True)
    parser.add_argument('--msp_path', help='path for msv file', required=True)
    args = parser.parse_args()

    homepage_url = args.homepage_url
    msp_path = args.msp_path

    upload_process_test(homepage_url, msp_path)


if __name__ == '__main__':
    main()
