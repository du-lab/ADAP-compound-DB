from selenium import webdriver
from selenium.webdriver.common.keys import Keys
import argparse


def initial_test(homepage_url)
    # Open the Chrome browser
    driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver.exe')

    # Open a web page
    driver.get(homepage_url)

    # Print out a title of the web page
    print(driver.title)

    assert driver.title == 'ADAP-KDB Compound Knowledgebase'

    # Close the browser
    driver.quit()

    print('Test is complete')


def main():
    """Main function that is called from a command line"""
    parser = argparse.ArgumentParser('Download all data into a folder')
    parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)

    args = parser.parse_args()

    homepage_url = args.homepage_url

    initial_test(homepage_url)

if __name__ == '__main__':
    main()
