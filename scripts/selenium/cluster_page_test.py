#!/usr/bin/env python3

"""this script is using for auto-testing cluster page"""
import argparse
import time
from selenium import webdriver
from urllib.parse import urljoin
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service as ChromeService

def cluster_page_test(homepage_url):
    driver = webdriver.Chrome(service=ChromeService('scripts/selenium/drivers/chromedriver'))

    try:
        driver.get(urljoin(homepage_url, "file/upload/?applicationMode=PRIORITIZE_SPECTRA"))

         # go to login page
        library_page_button = driver.find_element('id', 'libraryPage')
        library_page_button.click()

        # add 5 seconds delay for loading spectrum page before next step
        time.sleep(5)

        consensus_page_button = driver.find_element('id', 'consensusPage')
        consensus_page_button.click()
        time.sleep(10)

        # choose the first spectrum and go to the spectrum page
        cluster_table = driver.find_element('id', "cluster_table")
        spectra_list = cluster_table.find_elements(By.CSS_SELECTOR, "table>tbody>tr")

        # choose the first spectrum
        spectrum_link = spectra_list[1].find_element(By.CSS_SELECTOR, 'a')

        # the cluster id of the first spectrum
        table_cells = spectra_list[1].find_elements(By.CSS_SELECTOR, "td")
        cluster_id = table_cells[1].text

        spectrum_link.click()

        # add 5 seconds delay for loading cluster page before next step
        time.sleep(5)

        # validate if current url is account page
        assert(driver.current_url.__str__().startswith(urljoin(urljoin(homepage_url, 'cluster/'), cluster_id)))

        # get the Spectrum Plot span
        spectrum_plot_tab = driver.find_element(By.CSS_SELECTOR, 'a[href="#spectrum_plot"]')
        spectrum_plot_tab.click()

        # check if there is a spectrum plot
        assert(driver.find_element('id', 'plot'))

        # get the Distributions span
        distributions_tab = driver.find_element(By.CSS_SELECTOR, 'a[href="#tag_distributions"]')
        distributions_tab.click()

        # get the number of div tag in Distributions span, and test if exsit any figure.
        # if there is no figure, the total number of div tag will be 2
        div_list = driver.find_element('id', 'tag_distributions').find_elements(By.CSS_SELECTOR, 'div>div')
        assert(len(div_list) > 2)

        # get the Pie Chart span
        pie_chart_tab = driver.find_element(By.CSS_SELECTOR, 'a[href="#pie_chart"]')
        pie_chart_tab.click()

        # get the number of pie chart and check if there is at least one pie chart
        pie_chart_list = driver.find_element('id', "pie_chart").find_elements(By.CSS_SELECTOR, "div>div>div")
        assert(len(pie_chart_list) > 0)

        # get Spectrum List span
        spectrum_list_tab = driver.find_element(By.CSS_SELECTOR, 'a[href="#spectrum_list"]')
        spectrum_list_tab.click()

        # check if there is any records in the spectrum list table
        spectrum_table = driver.find_element('id', 'big_spectrum_table')
        assert(spectrum_table.find_elements(By.CSS_SELECTOR, 'table>tbody>tr'))

    except Exception as e:
        driver.quit()
        raise e


def main():
    """Main function that is called from a command line"""

    parser = argparse.ArgumentParser('parameter for testing')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)

    args = parser.parse_args()

    homepage_url = args.homepage_url

    cluster_page_test(homepage_url)

if __name__ == '__main__':
    main()
