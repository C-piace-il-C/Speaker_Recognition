function [] = PlotAllStats( statsAndrea, statsOddone, statsEmanuele )
stats = [statsAndrea zeros(16,8) statsOddone zeros(16,8) statsEmanuele];
PlotStatsMatrix(stats);
end

