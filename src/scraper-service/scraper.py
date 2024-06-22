import os
import requests
import asyncio
from bs4 import BeautifulSoup
PRODUCT_HOST = os.getenv('PRODUCT_SERVICE_HOST')


class Sites_config():
    config = {}
    def __init__(self, name):
        self.config =  {
            'COTO': {
                'url': f'https://www.cotodigital3.com.ar/sitios/cdigi/browse?_dyncharset=utf-8&Dy=1&Ntt={name}&Nty=1&Ntk=&siteScope=ok&_D%3AsiteScope=+&atg_store_searchInput={name}&idSucursal=200&_D%3AidSucursal=+&search=Ir&_D%3Asearch=+&_DARGS=%2Fsitios%2Fcartridges%2FSearchBox%2FSearchBox.jsp',
                'name': ['div', '', 0],
                'price': ['span', '', 3]
            }
        }


class Scraper():
    endpoints = []
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'}

    def __init__(self):
        self.endpoints = [f'http://{PRODUCT_HOST}:8080/product']

    def post_product(self, url, product):
        # posts product to url
        try:
            requests.post(url=url, json=product)
        except Exception as e:
            print(f'Error at post request of {product} to {url}')
            print(e)

    def to_price(self, s):
        # receives price, returns cents as int
        s = s.replace('$', '')
        s = s.replace('.', '')
        s = s.replace(',', '')
        return int(s)
    
    def test_send(self, url, product):
        print("SENDING")
        print(product)
    
    def run(self, name, sites):
        sites_config = Sites_config(name)
        paths = sites_config.config
        for site in paths.keys():
            if site in sites:
                path = paths[site]
                req = requests.get(url=path['url'], headers=self.headers)
                soup = BeautifulSoup(req.content, 'html.parser')

                for item in soup.find_all(class_='clearfix'):
                    # check discount
                    price = item.find(class_='price_discount_gde')
                    
                    # check normal price
                    if price is None:
                        price = item.find(class_='atg_store_newPrice')
                    
                    if price:
                        # if there is price then there is product
                        product_name = item.find(class_='descrip_full').text
                        price = self.to_price(price.text)
                        img = item.find(class_='atg_store_productImage').find('img').attrs['src']

                        product = {
                            'name': product_name,
                            'site': site,
                            'tracked': name,
                            'price': price,
                            'img': img,
                        }

                        for endpoint in self.endpoints:
                            self.post_product(url=endpoint, product=product)
                            #asyncio.run(self.post_product(url=endpoint, product=product))

