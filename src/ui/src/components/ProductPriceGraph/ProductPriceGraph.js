import axios from 'axios';
import { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './ProductPriceGraph.styles.css'
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
axios.defaults.baseURL = process.env.REACT_APP_API_HOST || 'http://ui.local';
dayjs.extend(utc);

const ProductPriceGraph = ({ productId }) => {
  const [priceData, setPriceData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Move fetch logic outside useEffect so it can be reused
  const fetchPriceHistory = async () => {
    if (!productId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      // Replace with your actual API endpoint
      const response = await axios.get(`product/${productId}`);
      
      
      const data = response.data;
      
      // Format the data if needed
      const formattedData = data.map(item => ({
        date: dayjs.utc(item.dateAdded).format('DD/MM/YY'),
        price: item.price/100,
        // You can add additional metrics if available
        // averageMarketPrice: item.averageMarketPrice,
      }));
      
      setPriceData(formattedData);
    } catch (err) {
      console.error("Failed to fetch price history:", err);
      console.log(err)
      console.error(err)
      setError("Failed to load price history data");
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    fetchPriceHistory();
  }, [productId]); // eslint-disable-line react-hooks/exhaustive-deps
  // Note: We're disabling the eslint warning as we're intentionally excluding fetchPriceHistory from dependencies

  if (loading) {
    return (
      <div className="graph-loading">
        <p>Loading price history...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="graph-error">
        <p>{error}</p>
        <button onClick={() => fetchPriceHistory()}>Try Again</button>
      </div>
    );
  }

  if (priceData.length === 0) {
    return (
      <div className="graph-no-data">
        <p>No price history available for this product</p>
      </div>
    );
  }

  return (
    <div className="product_graph_wrapper">
      <ResponsiveContainer width="100%" height={300}>
        <LineChart
          data={priceData}
          margin={{
            top: 5,
            right: 30,
            left: 20,
            bottom: 5,
          }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line 
            type="monotone" 
            dataKey="price" 
            stroke="#8884d8" 
            activeDot={{ r: 8 }} 
            name="Product Price"
          />
          {/* You can add additional lines if you have more data */}
          {/* <Line type="monotone" dataKey="averageMarketPrice" stroke="#82ca9d" name="Market Average" /> */}
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

export default ProductPriceGraph;