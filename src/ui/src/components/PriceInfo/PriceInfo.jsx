import axios from 'axios';
import dayjs from 'dayjs';
import { useState, useEffect } from 'react';
import './PriceInfo.styles.css'
axios.defaults.baseURL = 'http://ui.local';


const PriceInfo = ({metadataId}) => {
    const [price, setPrice] = useState("");
    const [date, setDate] = useState("");

    useEffect(() => {
        fetchPrice();
    }, []);

    const fetchPrice = async () => {
        try {
            const response = await axios.get(
                "product/" + metadataId
            );
            const data = response.data.at(-1);
            setPrice(data.price/100);
            setDate(dayjs(new Date(data.dateAdded)).format("DD/MM/YY"));
        } catch (error) {
            console.log("error fetching price");
        };
    };

    return (
        <div className="price">
            <span>Listed {date} at ${price} 
            </span>
        </div>
    )
}


export default PriceInfo