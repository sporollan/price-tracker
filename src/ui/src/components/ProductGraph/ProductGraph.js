import './ProductGraph.styles.css'


const ProductGraph = ({product}) => {
    return (
        <div className="product_graph_container">
            <h3>Price History Graph</h3>
            <div className="product_graph">
                {/* Call api to fetch data */}
                <p>Graph for {product.name} will be displayed here</p>
            </div>
        </div>
    )
}

export default ProductGraph;