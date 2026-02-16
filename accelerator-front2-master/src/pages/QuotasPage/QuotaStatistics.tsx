import React from 'react'
import { Paper, Typography, Box, LinearProgress } from '@mui/material'
import { Warning, CheckCircle, Error } from '@mui/icons-material'

const QuotaStatistics: React.FC = () => {
	const stats = {
		totalQuotas: 12,
		activeQuotas: 8,
		criticalQuotas: 2,
		warningQuotas: 3,
		normalQuotas: 7,
		totalLimit: 45200,
		totalUsed: 32850,
		utilization: 73,

		totalCompanyQuotas: 8,
		activeCompanyQuotas: 8,
		criticalCompanyQuotas: 2,
		warningCompanyQuotas: 2,
		normalCompanyQuotas: 4,
	}

	return (
		<Paper sx={{ p: 3 }}>
			<Typography variant='h6' gutterBottom sx={{ fontWeight: 'bold', mb: 3 }}>
				Общая статистика квот
			</Typography>

			<Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 4, mb: 4 }}>
				<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
						<Error color='error' />
						<Typography
							variant='h4'
							sx={{ fontWeight: 'bold', color: 'error.main' }}
						>
							{stats.criticalQuotas}
						</Typography>
					</Box>
					<Box>
						<Typography variant='body2' color='text.secondary'>
							Критичные квоты
						</Typography>
						<Typography variant='body2'>90% использовано</Typography>
					</Box>
				</Box>

				<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
						<Warning color='warning' />
						<Typography
							variant='h4'
							sx={{ fontWeight: 'bold', color: 'warning.main' }}
						>
							{stats.warningQuotas}
						</Typography>
					</Box>
					<Box>
						<Typography variant='body2' color='text.secondary'>
							Квоты с предупреждением
						</Typography>
						<Typography variant='body2'>75-90% использовано</Typography>
					</Box>
				</Box>

				<Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
					<Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
						<CheckCircle color='success' />
						<Typography
							variant='h4'
							sx={{ fontWeight: 'bold', color: 'success.main' }}
						>
							{stats.normalQuotas}
						</Typography>
					</Box>
					<Box>
						<Typography variant='body2' color='text.secondary'>
							Нормальные квоты
						</Typography>
						<Typography variant='body2'>75% использовано</Typography>
					</Box>
				</Box>
			</Box>

			{/* Общий прогресс */}
			<Box>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
					<Typography variant='body2' color='text.secondary'>
						Общее использование квот
					</Typography>
					<Typography variant='body2' sx={{ fontWeight: 'bold' }}>
						{stats.utilization}%
					</Typography>
				</Box>
				<LinearProgress
					variant='determinate'
					value={stats.utilization}
					color={
						stats.utilization >= 90
							? 'error'
							: stats.utilization >= 75
							? 'warning'
							: 'primary'
					}
					sx={{ height: 10, borderRadius: 5 }}
				/>
				<Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 1 }}>
					<Typography variant='caption' color='text.secondary'>
						{stats.totalUsed.toLocaleString()} кг использовано
					</Typography>
					<Typography variant='caption' color='text.secondary'>
						{stats.totalLimit.toLocaleString()} кг всего
					</Typography>
				</Box>
			</Box>
		</Paper>
	)
}

export default QuotaStatistics
