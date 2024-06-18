import axios from "axios"
import { useState, useEffect } from 'react'
import dayjs from "dayjs"
import './Products.css';

const ProductData = ({product}) => {
    const [products, setProducts] = useState([])
    const [toggle, setToggle] = useState(false)

    const handleFetchProductsData = async() => {
        if(!toggle) {
            fetchProductsData();
        }
        setToggle(!toggle);
    };

    const fetchProductsData = async () => {
        try {
            const response = await axios.get(
                "http://localhost:8080/product/" + product.metadataId,
            )
            setProducts(response.data)
        } catch (error){
            console.log("error fetching products")
        }
    };
    return (
        <div>
            <div onClick={handleFetchProductsData} className="product_name">
                {product.name}
            </div>
                <div className="products_data_list">
                    {products.map(
                        (productData) => (
                            toggle &&
                            <div className="product_data" key={productData.id}>
                                    <div className="product_data_individual">
                                        {dayjs(new Date(productData.dateAdded)).format("DD/MM/YY")}
                                    </div>
                                    <div className="product_data_individual">
                                        {"$" + productData.price/100}
                                    </div>
                            </div>
                        )
                    )}
                </div>
        </div>
    )
}



export default ProductData