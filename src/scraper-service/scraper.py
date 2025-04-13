import os
import requests
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
import time

from config import logger


PRODUCT_HOST = os.getenv('PRODUCT_SERVICE_HOST')
BATCH_SIZE = 10  # Number of products to batch before sending


class Sites_config():
    config = {}
    def __init__(self, name):
        self.config =  {
            'COTO': {
                'url': '',
                'name': ['div', '', 0],
                'price': ['span', '', 3]
            }
        }


class Scraper():
    endpoints = []
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'}
    chrome_options=None


    def __init__(self, debug=False):
        self.endpoints = [f'http://{PRODUCT_HOST}:8080/product']
        self.debug = debug
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        })
        # Configure Chrome options
        self.chrome_options = Options()
        self.chrome_options.add_argument("--headless=new")
        self.chrome_options.add_argument("--no-sandbox")
        self.chrome_options.add_argument("--disable-dev-shm-usage")
        self.chrome_options.add_argument("--disable-blink-features=AutomationControlled")
        self.chrome_options.add_argument("--window-size=1920,1080")
        self.chrome_options.add_argument("--disable-infobars")
        self.chrome_options.add_argument("--disable-extensions")
        self.chrome_options.add_argument("--disable-gpu")
        self.chrome_options.add_argument("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
        # Set lower memory limits
        self.chrome_options.add_argument("--js-flags=--max-old-space-size=512")
        # Reduce resource usage
        self.chrome_options.add_argument("--disk-cache-size=1")
        self.chrome_options.add_argument("--media-cache-size=1")
        self.chrome_options.add_argument("--disable-setuid-sandbox")
        self.driver = None


    def __del__(self):
        if self.driver:
            self.driver.quit()


    def build_url(self, name, start=0):
        name = name.replace(' ', '%2B')
        return f'''https://www.cotodigital.com.ar/sitios/cdigi/categoria%3FDy%3D1&Nf%3Dproduct.
                startDate%257CLTEQ%2B1.739232E12%257C%257Cproduct.endDate%257CGTEQ%2B1.739232E1
                2&No%3D{start}&Nr%3DAND%2528product.sDisp_200%253A1004%252Cproduct.language%253
                Aespa%25C3%25B1ol%252COR%2528product.siteId%253ACotoDigital%2529%2529&Nrpp%3D12
                &Ntt%3D{name}&format%3Djson'''.replace('\n','').replace(' ','')


    def to_price(self, s):
        # receives price, returns cents as int
        s = s.replace('$', '')
        s = s.replace('.', '')
        s = s.replace(',', '')
        return int(s)
    

    def print_product(self, url, product):
        # debug only
        logger.info(f"Product for {url}: {product}")


    def initialize_driver(self):
        if self.driver is None:
            self.driver = webdriver.Chrome(options=self.chrome_options)

    
    def post_products_batch(self, url, products):
        """Post a batch of products"""
        if not products:
            return
            
        try:
            if self.debug:
                logger.info(f"Would send {len(products)} products to {url}")
                for product in products:
                    self.print_product(url, product)
            else:
                response = self.session.post(url=url, json=products)
                logger.info(f"Posted {len(products)} products to {url}, status: {response.status_code}")
        except Exception as e:
            logger.error(f"Error posting batch to {url}: {str(e)}")


    def run(self, name, sites):
        sites_config = Sites_config(name)
        paths = sites_config.config

        self.initialize_driver()

        for site in paths.keys():
            # Skip sites not included in the argument
            if site not in sites:
                continue

            product_count = 0
            products_batch = []
            page = 1  # Track the current page number

            # Start by loading the initial search URL
            url = self.build_url(name=name, start=0)
            logger.info(f"Loading initial URL: {url}")
            self.driver.get(url)
            time.sleep(1)  # Allow the page to load
            
            while True:
                logger.info(f"Scraping page {page} on site: {site}")

                try:
                    WebDriverWait(self.driver, 10).until(
                        EC.presence_of_element_located((By.CLASS_NAME, "product-image"))
                    )
                except TimeoutException as e:
                    logger.warning(f"Timeout waiting for products on page {page}: {e}")
                    break

                products = self.driver.find_elements(By.CLASS_NAME, "producto-card")
                if not products:
                    logger.info(f"No products found on page {page}")
                    break

                # Process the found products
                for product in products:
                    try:
                        product_name = product.find_element(By.CLASS_NAME, "nombre-producto").text
                        price_element = product.find_element(By.CLASS_NAME, "card-title")
                        price = price_element.text if price_element else ""
                        image_element = product.find_element(By.CLASS_NAME, "product-image")
                        img = image_element.get_attribute("src") if image_element else ""

                        if price:
                            product_count += 1
                            new_product = {
                                'name': product_name,
                                'site': site,
                                'tracked': name,
                                'price': self.to_price(price),
                                'img': img,
                            }
                            products_batch.append(new_product)

                            if len(products_batch) >= BATCH_SIZE:
                                for endpoint in self.endpoints:
                                    self.post_products_batch(endpoint, products_batch)
                                products_batch = []
                    except Exception as e:
                        logger.error(f"Error processing product on page {page}: {str(e)}")

                # Log current URL before clicking next
                logger.info("Current URL before clicking next: " + self.driver.current_url)

                # Locate and click the "next" button using XPath
                try:
                    next_button = WebDriverWait(self.driver, 10).until(
                        EC.element_to_be_clickable((By.XPATH, "//a[contains(@class, 'page-back-next') and contains(text(), 'Siguiente')]"))
                    )
                    
                    # 1. Scroll to element with explicit alignment
                    self.driver.execute_script("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", next_button)
                    
                    # 2. Wait for any overlays/stale references to clear
                    time.sleep(0.5)
                    
                    # 3. Use JavaScript click to bypass overlay issues
                    self.driver.execute_script("arguments[0].click();", next_button)
                    
                    # 4. Wait for page load
                    WebDriverWait(self.driver, 10).until(
                        EC.staleness_of(next_button)
                    )
                    
                    page += 1
                    logger.info(f"Successfully navigated to page {page}")
                except TimeoutException:
                    logger.info(f"No next button found on page {page}. Ending scrape.")
                    break
                except Exception as e:
                    logger.error(f"Error clicking next: {str(e)}")
                    break

            # Post any remaining products after finishing pagination
            if products_batch:
                for endpoint in self.endpoints:
                    self.post_products_batch(endpoint, products_batch)
            
            logger.info(f"Completed {site} search for '{name}'. Found {product_count} products across {page} pages.")
