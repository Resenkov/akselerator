// CompanyQuotas.tsx
import React from 'react'
import {
	Paper,
	Typography,
	Box,
	LinearProgress,
	Chip,
	Stack,
	Card,
	CardContent,
} from '@mui/material'
import { Business, TrendingUp } from '@mui/icons-material'
import type { FishingQuota } from '../../interfaces'

interface CompanyQuotasProps {
	quotas: FishingQuota[]
}

const CompanyQuotas: React.FC<CompanyQuotasProps> = ({ quotas }) => {
	const getQuotaPercentage = (used: number, total: number) =>
		(used / total) * 100
	const getRemainingQuota = (used: number, total: number) => total - used

	const getProgressColor = (percentage: number) => {
		if (percentage >= 90) return 'error'
		if (percentage >= 75) return 'warning'
		return 'primary'
	}

	return (
		<Box
		//  sx={{ p: 3 }}
		>
			<Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
				{/* <Business color='primary' /> */}
				<Typography variant='h6' sx={{ fontWeight: 'bold' }}>
					Ваши квоты
				</Typography>
			</Box>

			<Stack spacing={2}>
				{quotas.map(quota => {
					const percentage = getQuotaPercentage(quota.usedKg, quota.limitKg)
					const remaining = getRemainingQuota(quota.usedKg, quota.limitKg)

					return (
						<Card key={quota.id} elevation={3}>
							<CardContent>
								<Box
									sx={{
										display: 'flex',
										justifyContent: 'space-between',
										alignItems: 'flex-start',
										mb: 2,
									}}
								>
									<Box>
										<Typography variant='h6' gutterBottom>
											{quota.speciesCommonName}
										</Typography>
										<Typography variant='body2' color='text.secondary'>
											{quota.regionName}
										</Typography>
										<Typography variant='body2' color='text.secondary'>
											{quota.periodStart} - {quota.periodEnd}
										</Typography>
									</Box>
									<Chip
										label={`Осталось: ${remaining.toLocaleString()} кг`}
										color={
											remaining < quota.limitKg * 0.1 ? 'error' : 'primary'
										}
										variant='outlined'
									/>
								</Box>

								<Box sx={{ mb: 1 }}>
									<Box
										sx={{
											display: 'flex',
											justifyContent: 'space-between',
											mb: 1,
										}}
									>
										<Typography variant='body2' color='text.secondary'>
											Использовано: {quota.usedKg.toLocaleString()} кг
										</Typography>
										<Typography variant='body2' color='text.secondary'>
											Всего: {quota.limitKg.toLocaleString()} кг
										</Typography>
									</Box>
									<LinearProgress
										variant='determinate'
										value={Math.min(percentage, 100)}
										color={getProgressColor(percentage)}
										sx={{ height: 8, borderRadius: 4 }}
									/>
								</Box>

								<Typography
									variant='body2'
									color='text.secondary'
									sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}
								>
									<TrendingUp fontSize='small' />
									{percentage.toFixed(1)}% квоты использовано
								</Typography>
							</CardContent>
						</Card>
					)
				})}
			</Stack>

			{quotas.length === 0 && (
				<Typography
					variant='body2'
					color='text.secondary'
					sx={{ textAlign: 'center', py: 3 }}
				>
					Нет доступных квот
				</Typography>
			)}
		</Box>
	)
}

export default CompanyQuotas
