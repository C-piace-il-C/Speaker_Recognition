function [ ] = PlotStatsMatrix( stats )

max = ceil(64/100*(80-33));
min = 1;
cmap = zeros(64,3);
cmap(1:min,1) = 1;
for n=min+1:max
    lerpFactor = 1/(max-min-1)*(n-min-1);
    cmap(n,1) = 1 - lerpFactor;
    cmap(n,2) = lerpFactor;
end
for n=max:64
    cmap(n,2) = 1;
end
colormap(cmap);
colorbar;
h = bar3(stats,1);
shading interp;
for i = 1:length(h)
    zdata = get(h(i),'ZData');
    set(h(i), 'CData', zdata); 
end
set(h, 'EdgeColor', 'black');
set(gca, 'CLim', [0, 1]);
zmax = 0.7;
axis([0 (size(stats,2)+1) 0 size(stats,1)+1 0 zmax])
end

