
let $makeupProducts := doc("products.xml")/Products/makeUpProduct
let $count := count($makeupProducts)
return
<result>
   <first>Count of products - {$count}</first>

   <second>
      {
	     for $product in $makeupProducts
         return $product/name
      }
   </second>

    <third>
        {
            for $product in $makeupProducts[3]
            return $product/brand
        }
    </third>

     <fourth>
        {
            for $product at $count in $makeupProducts
            where count(tokenize($product/name, "\w+?\s"))>7
            return $count
        }
    </fourth>

    <fifth>
        {
            for $product in $makeupProducts[2]
            return $product/*[1]
        }
    </fifth>

    <sixth>
        {
            for $product in $makeupProducts[2]
            return $product/*[2]
        }
    </sixth>

    <seventh>
        {
            for $product in $makeupProducts[2]
            return $product/*[3]
        }
    </seventh>

    <eighth>
        {
            for $product in $makeupProducts
            where ($product/reviews/comments>409100170) and ($product/reviews/comments<4091001709) and (matches($product/name, "гель"))
            return $product/name
        }
    </eighth>

    <ninth>
        {
            for $product at $count in $makeupProducts
            where $count mod 5 = 0
            return <res>{$product/name} {$product/status}</res>
        }
    </ninth>

    <tenth>
        {
            for $product at $count in $makeupProducts
            where $count mod 2 = 0
            return <res><pos>{$count}</pos> {$product/brand}</res>
        }
    </tenth>

</result>