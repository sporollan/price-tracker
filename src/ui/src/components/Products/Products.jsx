import { useState } from 'react';
import dayjs from 'dayjs';
import PriceInfo from "../PriceInfo/PriceInfo";
import ProductPriceGraph from '../ProductPriceGraph/ProductPriceGraph';
import './Products.styles.css';

const Products = ({ products }) => {
  // Track which product is expanded
  const [expandedProductId, setExpandedProductId] = useState(null);
  const [expandedProductRow, setExpandedProductRow] = useState(null);
  const [expandedProduct, setExpandedProduct] = useState(null);


  // Toggle expanded state
  const toggleExpand = (productId, rowNumber, product) => {
    if (expandedProductId === productId) {
      setExpandedProductId(null);
      setExpandedProductRow(null);
      setExpandedProduct(null);
    } else {
      setExpandedProductId(productId);
      setExpandedProductRow(rowNumber);
      setExpandedProduct(product);
    }
  };

  // Calculate grid positions
  const getProductPosition = (index) => {
    const productsPerRow = 5; // Adjust based on your design
    const row = Math.floor(index / productsPerRow);
    const column = index % productsPerRow;
    return { row, column };
  };

  // Organize products into rows for rendering
  const renderProducts = () => {
    const productsPerRow = 5; // Should match the CSS grid columns
    const productElements = [];
    let currentRow = -1;
    try {
      products.forEach((product, index) => {
        const { row, column } = getProductPosition(index);
        
        // Create a new row if needed
        if (row !== currentRow) {
          currentRow = row;
          productElements.push(
            <div key={`row-${row}`} className="products_row">
              {products.slice(row * productsPerRow, (row + 1) * productsPerRow).map((product) => (
                <div 
                  key={product.id} 
                  className={`product_item_container ${expandedProductId === product.id ? 'expanded' : ''}`}
                  onClick={() => toggleExpand(product.id, row, product)}
                >
                  <div className="product_name_container">
                    <span className="product_name">{product.name}</span>
                  </div>
                  <div className="price_info_container date_info">
                    <span>
                      Updated {dayjs(new Date(product.dateAdded)).format("DD/MM/YY")}
                    </span>
                  </div>
                  {/*
                  <div className="price_info_container">
                    <PriceInfo metadataId={product.id} />
                  </div>
                  */}
                  <div className="product_image_container">
                    <img className="product_image" src={product.img} alt={product.name} />
                  </div>
                </div>
              ))}
            </div>
          );
          
          // Add an expandable graph row that spans the full width
          productElements.push(
                <div 
                  className={`product_graph_container ${expandedProductRow === row ? 'visible' : 'hidden'}`}
                  key={product.id}
                >
                  {expandedProductRow === row && (
                    <div>
                      <h3>{expandedProduct.name}</h3>
                      <div className="product_graph">
                        <ProductPriceGraph productId={expandedProductId}/>
                      </div>
                    </div>
                  )}
                </div>
          );
        }
      });
    } catch (e) {
      console.log("Products is empty")
    }
    
    return productElements;
  };

  return (
    <div className="product_main_container">
      {renderProducts()}
    </div>
  );
};

export default Products;