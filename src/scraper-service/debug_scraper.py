from scraper import Scraper

if __name__=='__main__':
    s = Scraper(debug=True)
    s.run(name='cafe', sites=['COTO'])