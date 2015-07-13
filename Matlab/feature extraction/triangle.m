function v = triangle(x,m,M)
    if( x < m || x > M)
        v = 0;
        return
    end
    slope = 2 / (M-m);
    center = m + (M-m)/2;
    if( m < x && x < center )
        v = (x-m) * slope;
        return
    end
    v = 1-(x-center)*slope;
end