import axios from "axios"
import { useState } from 'react'
import ProductData from "../ProductData/ProductData"
import './Products.styles.css'

const Products = ({products}) => {

    return (
        <div className="product_main_container">
                {products.map(
                    (product) => (
                    <div className="product_item" key={product.id}>
                        <ProductData
                            product={{
                                name: product.name,
                                metadataId: product.id 
                            }}
                        />
                    </div>
                ))}
        </div>
    )
}



export default Products