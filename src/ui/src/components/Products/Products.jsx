import axios from "axios"
import { useState } from 'react'
import ProductData from "../ProductData/ProductData"
import './Products.styles.css'

const Products = ({products}) => {

    return (
        <div className="product_main_container">
                {products.map(
                    (product) => (
                    <div className="product_item_container" key={product.id}>
                        <div className="product_name_container">
                            <span className="product_name">{product.name}</span>
                        </div>
                        <div className="product_image_container">
                            <img className="product_image" src={product.img}/>
                        </div>
                    </div>
                ))}
        </div>
    )
}



export default Products