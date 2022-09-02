from selenium import webdriver
import argparse
import time

def manual_search_test(homepage_url, identifier = 'eicosatrienoic acid', spectrum = '79 100\n 67 90.3325\n 80 75.8722\n 81 59.0838\n 93 54.0933'):
	driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

	try:
		driver.get(homepage_url)
		search_page_button = driver.find_element_by_id('manualSearchPage')
		search_page_button.click()
		identifier_input = driver.find_element_by_id('identifierInput')
		identifier_input.send_keys(identifier)
		spectrum_input = driver.find_element_by_id('spectrumInput')
		spectrum_input.send_keys(spectrum)
		search_button = driver.find_element_by_id('searchButton')
		search_button.click()
		time.sleep(30)
		assert (driver.find_element_by_id('plot'))

	except Exception as e:
		#driver.quit()
		raise e


def main():
	"""Main function that is called from a command line"""

	parser = argparse.ArgumentParser('Download all data into a folder')
	parser.add_argument('--homepage-url', help='url for adap-kdb homepage', required=True)
	parser.add_argument('--identifier', help='identifier string', required=True)
	parser.add_argument('--spectrum', help='spectrum string', required=True)
	args = parser.parse_args()
	manual_search_test(args.homepage_url, args.identifier, args.spectrum)

if __name__ == '__main__':
	main()