# Integration Analysis — GameZone Unicesar

This analysis is based on the Requirement 5 - Integration, that integrates all of the following branches into the `develop` branch.

| Phase | Module | Adjustments |
|---|---|---|
| 1 | Accessories (`feature/accessory-module`) | — |
| 2 | Promotions (`feature/promotion-module`) | A1 |
| 3 | Warranties (`feature/warranty-module`) | A2, A3 |
| 4 | Returns (`feature/return-module`) | A4, A5, A6, A7 |
| 5 | Closing | A8, A9 |

---

## A1 — `feat:` Category discount for accessories
**Type:** new feature · **Phase:** 2 · **Branch:** `feature/accessory-category-discount`

### Situation
Requirement 2 limited the target category of `CategoryDiscount` to `"VIDEOGAME"` and
`"CONSOLE"`. With the accessory module integrated, the store needed to launch
promotions for accessories, but there was no way to do it.

### Changes applied
- `CategoryDiscount` accepts `"ACCESSORY"` as a target category and treats every
  instance of `Accessory` as part of it in `calculateDiscount`.
- `PromotionService.registerCategoryDiscount` only allows the three valid categories
  (`VIDEOGAME`, `CONSOLE`, `ACCESSORY`) and rejects any other value.
- `PromotionPanel` includes the "Accesorio" option when registering a category promotion.
- `data/promotions.csv` includes an active accessory promotion ("25% en accesorios").

---

## A2 — `fix:` Warranty circular dependency
**Type:** fix · **Phase:** 3 · **Branch:** `fix/warranty-circular-dependency`

### Situation
With the Requirement 4 design, `WarrantyRepository` needed to resolve sales while
loading, which created the cycle `SaleService → WarrantyService → WarrantyRepository →
SaleService`. The cycle prevented building the objects in `MainFrame` through
constructor injection.

### Changes applied
- `WarrantyRepository` saves and loads only the identifiers of the sale and the product.
  Loading returns `WarrantyRecord` objects and the class no longer depends on any service.
- `WarrantyService` receives `WarrantyRepository`, `SalePersistence` and `ProductService`
  and resolves the `Sale` and `Product` references from the identifiers.
- `MainFrame` builds the objects in the new order and connects the warranty panel.
- `docs/warranty-class-diagram.md` shows the new dependencies.

---

## A3 — `refactor:` Unified sale registration flow
**Type:** refactor · **Phase:** 3 · **Branch:** `refactor/unified-sale-registration`

### Situation
Requirements 1, 2 and 4 modified `SaleService.registerSale` independently. Together,
the order of the operations changed the result (for example, whether the discount was
applied before or after the cost of the warranties).

### Changes applied
- `SaleService.registerSale` follows this order: validate that the sale has at least one
  item; resolve each item as a product or an accessory and validate its stock; create the
  sale and calculate the subtotal; apply the best active promotion, calculated only on the
  subtotal; generate the basic warranty of each console and the requested extended
  warranties, adding their cost; calculate the final total (subtotal − discount + extended
  warranty cost); update the inventory through `ProductService` or `AccessoryService`;
  persist the sale and the warranties.
- `Sale.receiptGen` shows the subtotal, the discount with the promotion name, the cost of
  the extended warranties and the final total.
- `SalePanel` lets the user select products and accessories and asks for the extended
  warranty of each console.

---

## A4 — `fix:` Accessory returns
**Type:** fix · **Phase:** 4 · **Branch:** `fix/return-accessory-stock`

### Situation
Requirement 3 restored the stock by calling only `ProductService.restoreStock`, which does
not manage accessories. When an accessory was returned, its stock was not restored. In
addition, `ProductService.restoreStock` did not save the change to the file.

### Changes applied
- `ReturnService` receives `AccessoryService` through its constructor and restores the
  stock with the service that owns each returned item.
- `AccessoryService.restoreStock(String accessoryId, int quantity)` was added.
- `ProductService.restoreStock` persists the new stock.
- `ReturnPersistence` resolves accessories when loading returns.

---

## A5 — `fix:` Refund of discounted sales
**Type:** fix · **Phase:** 4 · **Branch:** `fix/return-discounted-refund`

### Situation
`Return.calculateRefundAmount` added the list prices of the returned items. If the
original sale had a promotion, the customer got back more than what was paid.

### Changes applied
- `Return.calculateRefundAmount` refunds each item in proportion to the discount of the
  original sale: `price × (1 − discount / subtotal)`, using the private method
  `getPaidFactor()`.
- `Return.generateReturnReceipt` shows, for each item, its list price, its share of the
  discount and the refunded amount.

---

## A6 — `fix:` Monthly balance report
**Type:** fix · **Phase:** 4 · **Branch:** `fix/monthly-balance-report`

### Situation
Requirement 3 asks to show the total sales, the total returns and the net balance, but
`generateMonthlyBalance` only returned the net balance. With promotions and warranties
integrated, the total sales must use the final total of each sale. The returns panel also
passed the month and the year in the wrong order.

### Changes applied
- `ReturnService` adds `calculateMonthlySales(int month, int year)`, which uses the final
  total of each sale, and `calculateMonthlyReturns(int month, int year)`.
- `generateMonthlyBalance` keeps its signature and returns the difference between both.
- `ReturnPanel` shows the three values and calls the methods with the correct argument order.

---

## A7 — `feat:` Warranty cancellation when a console is returned
**Type:** new feature · **Phase:** 4 · **Branch:** `feature/return-warranty-cancellation`

### Situation
None of the requirements defined what happens to the warranty of a returned console. In
the integrated system, a returned console cannot keep an active warranty.

### Changes applied
- `WarrantyService.cancelWarranties(String productId, String saleId)` removes the
  warranties of the product in that sale and returns the refundable cost (zero for the
  basic warranty, the additional cost for the extended one).
- `ReturnService.registerReturn` calls it for each returned console and includes the
  value in the refund.
- `Return` stores the warranty refund, adds it to the refund calculation and shows it in
  the receipt. `ReturnPersistence` saves and loads it.
- `MainFrame` injects `WarrantyService` into `ReturnService`.