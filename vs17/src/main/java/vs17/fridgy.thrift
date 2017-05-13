namespace java fridgy

struct PriceRequest {
    1: string product
}

struct PurchaseRequest {
    1: string product,
    2: i32 qty
}

service Store {
    i32 requestPrice(1:PriceRequest request),
    oneway void purchase(1:PurchaseRequest request)
}
