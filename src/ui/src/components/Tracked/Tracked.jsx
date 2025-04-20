import axios from 'axios';
import { useState, useEffect , useCallback} from 'react';
import './Tracked.styles.css'

// Configure axios defaults for the host
axios.defaults.baseURL = process.env.REACT_APP_API_HOST || 'http://ui.local';

const Tracked = ({setProducts}) => {
    const [trackedProducts, setTrackedProducts] = useState([]);
    const [newTrackedProduct, setNewTrackedProduct] = useState("");
    const [selectedTracked, setSelectedTracked] = useState(null);
    const [page, setPage]       = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const size = 40;
    const [isLoading, setIsLoading] = useState(false);
    const token  = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    useEffect(() => {
        axios.get('tracked', {
          params: { user: userId },
          headers: { Authorization: `Bearer ${token}` }
        })
        .then(({ data }) => setTrackedProducts(data))
        .catch(e => console.error("error fetching tracked products", e));
      }, [userId, token]);
    
      // the paged-fetch function
      const fetchProductsPage = useCallback(async (trackedName, pageToLoad) => {
        if (isLoading) return;
        setIsLoading(true);
    
        try {
          // on first page of a new term, reset
          if (pageToLoad === 0) {
            setProducts([]);
            setHasMore(true);
            setSelectedTracked(trackedName);
          }
    
          const { data } = await axios.get(
            `/productMetadata/${trackedName}`,
            { params: { page: pageToLoad, size } }
          );
    
          // append or reset
          setProducts(prev =>
            pageToLoad === 0 ? data.content : [...prev, ...data.content]
          );
          setPage(pageToLoad + 1);
    
          // derive hasMore:
          let nextHasMore;
          if (typeof data.hasNext === 'boolean') {
            nextHasMore = data.hasNext;
          } else if (typeof data.last === 'boolean') {
            nextHasMore = !data.last;
          } else {
            // fallback: if we got a “full” page back, assume more
            nextHasMore = data.content.length === size;
          }
          setHasMore(nextHasMore);
    
        } catch (err) {
          console.error("error fetching products", err);
        } finally {
          setIsLoading(false);
        }
      }, [isLoading, setProducts]);
    
      // whenever a new term is clicked, load page 0
      const onClickTracked = (e, term) => {
        e.preventDefault();
        fetchProductsPage(term, 0);
      };
    
      // infinite‑scroll: when you hit near the bottom, load next page
      useEffect(() => {
        if (!selectedTracked || !hasMore) return;   // only bind if there IS more
    
        const onScroll = () => {
          const threshold = 300; // px from bottom
          if (
            window.innerHeight + window.scrollY >= document.body.offsetHeight - threshold
            && !isLoading
          ) {
            fetchProductsPage(selectedTracked, page);
          }
        };
        window.addEventListener('scroll', onScroll);
        return () => window.removeEventListener('scroll', onScroll);
      }, [selectedTracked, page, hasMore, isLoading, fetchProductsPage]);

    const fetchTrackedProducts = async () => {
        try {
            const response = await axios.get(
                `tracked`,
                {
                    params: {
                        user: userId
                    },
                    headers: {
                      Authorization: `Bearer ${token}`,
                      "Content-Type": "application/json"
                    },
                }
            );
            setTrackedProducts(response.data);
        }catch (error){
            console.log("error fetching tracked products")
            console.log(error)
        };
    };

    const toggleTrackedProduct = async (event, id) => {
        try {
            await axios.put(
                `toggle`,
                {},
                {
                    params: {
                        user: userId,
                        id: id
                    },
                    headers: {
                      Authorization: `Bearer ${token}`,
                      "Content-Type": "application/json"
                    },
                }
            )
        }catch (error){
            console.log("error toggling product " + id)
        }
        fetchTrackedProducts()
    };

    const handleSubmitTrackedProduct = async () => {
        try {
            console.log(newTrackedProduct)
            await axios.post(
                "tracked",
                {
                    name: newTrackedProduct,
                    sites: "COTO",
                    users: [userId]
                },
                {
                    headers: {
                      Authorization: `Bearer ${token}`,
                      "Content-Type": "application/json"
                    },
                }
            )
            fetchTrackedProducts()
        }catch (error){
            console.log("error posting tracked product")
        };
    };

    const handleUpdateProducts = async () => {
        try {
            await axios.post(
                "run_scraper",
                {},
                {
                    headers: {
                      Authorization: `Bearer ${token}`,
                      "Content-Type": "application/json"
                    },
                }
            );
        } catch (error){
            console.log("error updating products")
            alert("error running scraper")
            alert(token)
        }
    };

    return (
        <div className='tracked_container_list'>
            <div className='tracked_header_container'>
                <div className='tracked_header'>
                    <span>Tracked Products</span>
                </div>
                <button onClick={handleUpdateProducts}>Update</button>
            </div>
            <div className='tracked_row'>
                <input
                className="tracked_left"
                value={newTrackedProduct}
                onChange={e => setNewTrackedProduct(e.target.value)}
                placeholder='New Product...'
                />                
                <button onClick={handleSubmitTrackedProduct}>Add</button>
            </div>

            <div className='tracked_item_list_container'>
            {trackedProducts.map(item => (
          <div className='tracked_row' key={item.id}>
            <div
              className='tracked_left'
              onClick={e => onClickTracked(e, item.name)}
            >
              {item.name}
            </div>
            <div className='tracked_right'>
                <button onClick={event => toggleTrackedProduct(event, item.id)}>Track</button>
            </div>
          </div>
        ))}
            </div>
            <div className='tracked_row'>
                <div>
                </div>
            </div>
        </div>
    )
}

export default Tracked;
