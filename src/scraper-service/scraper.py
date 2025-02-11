import os
import requests
import asyncio
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
PRODUCT_HOST = os.getenv('PRODUCT_SERVICE_HOST')


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


    def post_product(self, url, product):
        # posts product to url
        try:
            requests.post(url=url, json=product)
        except Exception as e:
            print(f'Error at post request of {product} to {url}')
            print(e)


    def build_url(self, name, start=0):
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
        print(f"SENDING TO {url}")
        print(product)
    
    def run(self, name, sites):
        sites_config = Sites_config(name)
        paths = sites_config.config
        for site in paths.keys():
            # Start Scraping Product on each site
            if site in sites:
                page=0
                stop = False
                while page < 10 and not stop:
                    url=self.build_url(name=name, start=page*12)
                    page=page+1
            
                    # Get response
                    driver = webdriver.Chrome(options=self.chrome_options)
                    driver.get(url)

                    # Wait for products to load
                    WebDriverWait(driver, 15).until(
                        EC.presence_of_element_located((By.CLASS_NAME, "product-image"))
                    )

                    # Extract product data
                    products = driver.find_elements(By.CLASS_NAME, "producto-card")
                    for product in products:
                        product_name = product.find_element(By.CLASS_NAME, "nombre-producto").text
                        price = product.find_element(By.CLASS_NAME, "card-title").text
                        image_element = product.find_element(By.CLASS_NAME, "product-image")
                        img = image_element.get_attribute("src")
                        print(f"Product: {product_name} | Price: {price}")
                        if price:
                            new_product = {
                                'name': product_name,
                                'site': site,
                                'tracked': name,
                                'price': self.to_price(price),
                                'img': img,
                            }
                            for endpoint in self.endpoints:
                                if self.debug:
                                    self.print_product(url=endpoint, product=new_product)
                                else:
                                    self.post_product(url=endpoint, product=new_product)
                                #asyncio.run(self.post_product(url=endpoint, product=product))
                        else:
                            stop = True

                driver.quit()
