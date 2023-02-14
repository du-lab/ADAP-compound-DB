import argparse
import time

from selenium import webdriver
from selenium.webdriver.common.by import By


def manual_search_test(homepage_url, identifier = 'eicosatrienoic acid', spectrum = '79 100\n 67 90.3325\n 80 75.8722\n 81 59.0838\n 93 54.0933'):
	driver = webdriver.Chrome('scripts/selenium/drivers/chromedriver')

	try:
		driver.get(homepage_url)
		driver.find_element(By.ID, 'manualSearchPage').click()
		driver.find_element(By.ID, 'identifierInput').send_keys(identifier)
		driver.find_element(By.ID, 'spectrumInput').send_keys(spectrum)
		driver.find_element(By.ID, 'parametersTab').click()
		time.sleep(5)
		driver.find_element(By.NAME, 'scoreThreshold').clear()
		driver.find_element(By.NAME, 'retentionIndexTolerance').clear()
		driver.find_element(By.NAME, 'mzTolerance').clear()
		driver.find_element(By.NAME, 'limit').clear()
		driver.find_element(By.NAME, 'scoreThreshold').send_keys('1.5')
		driver.find_element(By.ID, 'searchButton').click()
		time.sleep(10)
		assert (driver.find_element(By.ID, 'plot'))

	except Exception as e:
		driver.quit()
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